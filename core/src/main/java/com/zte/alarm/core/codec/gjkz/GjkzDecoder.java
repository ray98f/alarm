package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.pojo.Alarm;
import com.zte.alarm.core.pojo.AlarmList;
import com.zte.alarm.core.pojo.AlarmMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 17:14
 */
public class GjkzDecoder extends ReplayingDecoder<GjkzDecoder.DecoderState> {

    private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;
    private GjkzFixedHeader gjkzFixedHeader;
    private Object variableHeader;
    private int bytesRemainingInVariablePart;
    private final int maxBytesInMessage;

    public GjkzDecoder() {
        this(8092);
    }

    public GjkzDecoder(int maxBytesInMessage) {
        super(DecoderState.READ_FIXED_HEADER);
        this.maxBytesInMessage = maxBytesInMessage;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        GjkzDecoder.Result decodedPayload;
        switch ((GjkzDecoder.DecoderState) this.state()) {
            case READ_FIXED_HEADER:
                try {
                    this.gjkzFixedHeader = decodeFixedHeader(ctx, buffer);
                    this.bytesRemainingInVariablePart = this.gjkzFixedHeader.remainingLength();
                    this.checkpoint(GjkzDecoder.DecoderState.READ_VARIABLE_HEADER);
                } catch (Exception var8) {
                    out.add(this.invalidMessage(var8));
                    return;
                }
            case READ_VARIABLE_HEADER:
                try {
                    decodedPayload = this.decodeVariableHeader(ctx, buffer, this.gjkzFixedHeader);
                    this.variableHeader = decodedPayload.value;
                    if (this.bytesRemainingInVariablePart > this.maxBytesInMessage) {
                        throw new TooLongFrameException("可变报头解码异常: " + this.bytesRemainingInVariablePart);
                    }

                    this.bytesRemainingInVariablePart -= decodedPayload.numberOfBytesConsumed;
                    this.checkpoint(GjkzDecoder.DecoderState.READ_PAYLOAD);
                } catch (Exception var7) {
                    out.add(this.invalidMessage(var7));
                    return;
                }
            case READ_PAYLOAD:
                try {
                    decodedPayload = decodePayload(ctx, buffer, this.gjkzFixedHeader.messageType(), this.bytesRemainingInVariablePart, this.variableHeader);
                    this.bytesRemainingInVariablePart -= decodedPayload.numberOfBytesConsumed;
                    if (this.bytesRemainingInVariablePart != 0) {
                        throw new DecoderException("有效载荷解码异常: " + this.bytesRemainingInVariablePart + " (" + this.gjkzFixedHeader.messageType() + ')');
                    }

                    this.checkpoint(GjkzDecoder.DecoderState.READ_FIXED_HEADER);
                    GjkzMessage message = GjkzMessageFactory.newMessage(this.gjkzFixedHeader, this.variableHeader, decodedPayload.value);
                    this.gjkzFixedHeader = null;
                    this.variableHeader = null;
                    out.add(message);
                    break;
                } catch (Exception var6) {
                    out.add(this.invalidMessage(var6));
                    return;
                }
            case BAD_MESSAGE:
                buffer.skipBytes(this.actualReadableBytes());
                break;
            default:
                throw new Error();
        }
    }

    private GjkzMessage invalidMessage(Throwable cause) {
        this.checkpoint(GjkzDecoder.DecoderState.BAD_MESSAGE);
        return GjkzMessageFactory.newInvalidMessage(this.gjkzFixedHeader, this.variableHeader, cause);
    }

    private static GjkzFixedHeader decodeFixedHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        short b1 = buffer.readUnsignedByte();
        GjkzMessageType messageType = GjkzMessageType.valueOf(b1);

        int remainingLength = 0;
        int multiplier = 1;
        int loops = 0;

        short digit;
        try {
            do {

                digit = buffer.readUnsignedByte();
                remainingLength += (digit & 127) * multiplier;
                multiplier *= 128;
                ++loops;
            } while ((digit & 128) != 0 && loops < 4);

            if (loops == 4 && (digit & 128) != 0) {
                throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
            } else {
                GjkzFixedHeader decodedFixedHeader = new GjkzFixedHeader(messageType, remainingLength);
                return GjkzCodecUtil.validateFixedHeader(ctx, GjkzCodecUtil.resetUnusedFields(decodedFixedHeader));
            }
        } catch (Exception ex) {
            int i = 0;
            return null;
        }
    }

    public static int getByteLengthInt(ByteBuf buffer) {
        int remainingLength = 0;
        int multiplier = 1;
        int loops = 0;
        short digit;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            ++loops;
        } while ((digit & 128) != 0 && loops < 4);
        return remainingLength;
    }


    private GjkzDecoder.Result<?> decodeVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer, GjkzFixedHeader gjkzFixedHeader) {
        switch (gjkzFixedHeader.messageType()) {
            case CONNECT:
                return decodeConnectionVariableHeader(ctx, buffer);
            case CONNACK:
                return decodeConnAckVariableHeader(ctx, buffer);
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
            case ALARMSYNCREQ:
                return new GjkzDecoder.Result((Object) null, 0);
            case ALARMSYNCRESP:
                return decodeAlarmSyncRespVariableHeader(ctx, buffer);
            case ALARMREQ:
                return decodeAlarmReqVariableHeader(ctx, buffer);
            case ALARMRESP:
                return decodeAlarmRespVariableHeader(ctx, buffer);
            case CONTROLREQ:
                return decodeControlReqVariableHeader(ctx, buffer);
            case CONTROLRESP:
                return decodeControlRespVariableHeader(ctx, buffer);
            case CRESULTREQ:
                return decodeCResultReqVariableHeader(ctx, buffer);
            case CRESULTRESP:
                return decodeCResultRespVariableHeader(ctx, buffer);
            case READREQ:
                return decodeReadReqVariableHeader(ctx, buffer);
            case READRESP:
                return decodeReadRespVariableHeader(ctx, buffer);
            default:
                throw new DecoderException("未知的消息类型: " + gjkzFixedHeader.messageType());
        }
    }

    private static GjkzDecoder.Result<?> decodePayload(ChannelHandlerContext ctx, ByteBuf buffer, GjkzMessageType messageType, int bytesRemainingInVariablePart, Object variableHeader) {
        switch (messageType) {
            case CONNECT:
                return decodeConnectionPayload(buffer, (GjkzConnectVariableHeader) variableHeader);
            case ALARMSYNCRESP:
                return decodeAlarmSyncRespPayload(buffer, (GjkzAlarmSyncRespVariableHeader) variableHeader);
            case ALARMREQ:
                return decodeAlarmReqPayload(buffer, bytesRemainingInVariablePart, (GjkzAlarmReqVariableHeader) variableHeader);
            case CONNACK:
            case PINGRESP:
            case PINGREQ:
            case DISCONNECT:
            case ALARMSYNCREQ:
            case ALARMRESP:
            case CONTROLRESP:
            case CRESULTRESP:
            default:
                return new GjkzDecoder.Result((Object) null, 0);
            case CONTROLREQ:
                return decodeControlReqPayload(buffer, (GjkzControlReqVariableHeader) variableHeader);
            case CRESULTREQ:
                return decodeCResultReqPayload(buffer, (GjkzCResultReqVariableHeader) variableHeader);
            case READREQ:
                return decodeReadReqPayload(buffer, (GjkzReadReqVariableHeader) variableHeader);
            case READRESP:
                return decodeReadRespPayload(buffer, (GjkzReadRespVariableHeader) variableHeader);
        }
    }


    private static GjkzDecoder.Result<GjkzConnectVariableHeader> decodeConnectionVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        GjkzDecoder.Result<String> protoString = decodeString(buffer);
        int numberOfBytesConsumed = protoString.numberOfBytesConsumed;
        byte protocolLevel = buffer.readByte();
        ++numberOfBytesConsumed;
//        GjkzVersion version = GjkzVersion.fromProtocolNameAndLevel((String) protoString.value, protocolLevel);
//        GjkzCodecUtil.setGjkzVersion(ctx, version);
        int b1 = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int keepAlive = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        boolean hasUserName = (b1 & 128) == 128;
        boolean hasPassword = (b1 & 64) == 64;
        boolean flag5 = (b1 & 32) == 32;
        boolean flag4 = (b1 & 16) == 16;
        boolean flag3 = (b1 & 8) == 8;
        boolean flag2 = (b1 & 4) == 4;
        boolean flag1 = (b1 & 2) == 2;
        boolean flag0 = (b1 & 1) == 1;


        GjkzConnectVariableHeader gjkzConnectVariableHeader = new GjkzConnectVariableHeader(protoString.value, protocolLevel, hasUserName, hasPassword, flag5, flag4, flag3, flag2, flag1, flag0, keepAlive);
        return new GjkzDecoder.Result(gjkzConnectVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzConnAckVariableHeader> decodeConnAckVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;

        GjkzConnectReturnCode gjkzConnectReturnCode = GjkzConnectReturnCode.valueOf(buffer.readByte());
        ++numberOfBytesConsumed;

        GjkzConnAckVariableHeader gjkzConnAckVariableHeader = new GjkzConnAckVariableHeader(gjkzConnectReturnCode);
        return new GjkzDecoder.Result(gjkzConnAckVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzAlarmSyncRespVariableHeader> decodeAlarmSyncRespVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;

        int totalNumberOfAlarms = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        int numberOfAlarmsInThisPacket = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int totalNumberOfPackets = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int currentPackageNumber = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzAlarmSyncRespVariableHeader gjkzAlarmSyncRespVariableHeader = new GjkzAlarmSyncRespVariableHeader(totalNumberOfAlarms, numberOfAlarmsInThisPacket, totalNumberOfPackets, currentPackageNumber, messageIdentifier);
        return new GjkzDecoder.Result(gjkzAlarmSyncRespVariableHeader, numberOfBytesConsumed);
    }

    private static Result<GjkzAlarmReqVariableHeader> decodeAlarmReqVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;

        int year = decodeYear(buffer);
        numberOfBytesConsumed += 2;
        int month = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int day = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int hour = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int minute = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int second = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute, second);

        int system = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int line = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int station = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int device = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        int slot = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;

        int b1 = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        boolean isRecovery = (b1 & 32768) == 32768;
        int code = b1 & 32767;

        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzAlarmReqVariableHeader gjkzAlarmReqVariableHeader = new GjkzAlarmReqVariableHeader(messageIdentifier,
                new Alarm(time, system, line, station, device, slot, isRecovery, code, new ArrayList<AlarmMessage>()));
        return new GjkzDecoder.Result(gjkzAlarmReqVariableHeader, numberOfBytesConsumed);

    }


    private Result<GjkzAlarmRespVariableHeader> decodeAlarmRespVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;

        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        GjkzAlarmRespVariableHeader gjkzAlarmRespVariableHeader = new GjkzAlarmRespVariableHeader(messageIdentifier);
        return new GjkzDecoder.Result(gjkzAlarmRespVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzControlReqVariableHeader> decodeControlReqVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int version = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;
        int device = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        int functionCode = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzControlReqVariableHeader gjkzControlReqVariableHeader = new GjkzControlReqVariableHeader(version, device, functionCode, messageIdentifier);
        return new GjkzDecoder.Result(gjkzControlReqVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzControlRespVariableHeader> decodeControlRespVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzControlRespVariableHeader gjkzControlRespVariableHeader = new GjkzControlRespVariableHeader(messageIdentifier);
        return new GjkzDecoder.Result(gjkzControlRespVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzCResultReqVariableHeader> decodeCResultReqVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzCResultReqVariableHeader gjkzCResultReqVariableHeader = new GjkzCResultReqVariableHeader(messageIdentifier);
        return new GjkzDecoder.Result(gjkzCResultReqVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzCResultRespVariableHeader> decodeCResultRespVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzCResultRespVariableHeader gjkzCResultRespVariableHeader = new GjkzCResultRespVariableHeader(messageIdentifier);
        return new GjkzDecoder.Result(gjkzCResultRespVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzReadReqVariableHeader> decodeReadReqVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int version = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;

        int device = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        int functionCode = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzReadReqVariableHeader gjkzReadReqVariableHeader = new GjkzReadReqVariableHeader(version, device, functionCode, messageIdentifier);
        return new GjkzDecoder.Result(gjkzReadReqVariableHeader, numberOfBytesConsumed);
    }

    private Result<GjkzReadRespVariableHeader> decodeReadRespVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        int numberOfBytesConsumed = 0;
        int messageIdentifier = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        GjkzReadRespVariableHeader gjkzReadRespVariableHeader = new GjkzReadRespVariableHeader(messageIdentifier);
        return new GjkzDecoder.Result(gjkzReadRespVariableHeader, numberOfBytesConsumed);
    }


    private static GjkzDecoder.Result<GjkzConnectPayload> decodeConnectionPayload(ByteBuf buffer, GjkzConnectVariableHeader gjkzConnectVariableHeader) {
        GjkzDecoder.Result<String> decodedClientId = decodeString(buffer);
        String decodedClientIdValue = (String) decodedClientId.value;

        if (!GjkzCodecUtil.isValidClientId(decodedClientIdValue)) {
            throw new GjkzIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
        } else {
            int numberOfBytesConsumed = decodedClientId.numberOfBytesConsumed;


            GjkzDecoder.Result decodedUserName = null;
            String userName = null;
            byte[] decodedPassword = null;
            if (gjkzConnectVariableHeader.isHasUserName()) {
                decodedUserName = decodeString(buffer);
                userName = (String) decodedUserName.value;
                numberOfBytesConsumed += decodedUserName.numberOfBytesConsumed;
            }

            if (gjkzConnectVariableHeader.isHasPassword()) {
                decodedPassword = decodeByteArray(buffer);
                numberOfBytesConsumed += decodedPassword.length + 2;
            }

            GjkzConnectPayload gjkzConnectPayload = new GjkzConnectPayload(decodedClientIdValue, userName, decodedPassword);
            return new GjkzDecoder.Result(gjkzConnectPayload, numberOfBytesConsumed);
        }
    }

    private static GjkzDecoder.Result<GjkzAlarmSyncRespPayload> decodeAlarmSyncRespPayload(ByteBuf buffer, GjkzAlarmSyncRespVariableHeader gjkzAlarmSyncRespVariableHeader) {
        int numberOfBytesConsumed = 0;

        AlarmList alarmList = new AlarmList();

        if (gjkzAlarmSyncRespVariableHeader.getNumberOfAlarmsInThisPacket() != 0) {
            for (int i = 1; i <= gjkzAlarmSyncRespVariableHeader.getNumberOfAlarmsInThisPacket(); i++) {
                int alarmIndex = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                if (i != alarmIndex) {
                    throw new DecoderException("包序号不正确，期望包序号：" + i + ",实际包序号：" + alarmIndex);
                }

                int alarmPackageLength = decodeMsbLsb(buffer);
                numberOfBytesConsumed += 2;

                int year = decodeYear(buffer);
                numberOfBytesConsumed += 2;
                int month = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int day = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int hour = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int minute = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int second = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute, second);

                int system = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int line = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int station = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;
                int device = decodeMsbLsb(buffer);
                numberOfBytesConsumed += 2;
                int slot = buffer.readUnsignedByte();
                ++numberOfBytesConsumed;


                int b1 = decodeMsbLsb(buffer);
                numberOfBytesConsumed += 2;
                boolean isRecovery = (b1 & 32768) == 32768;
                int code = b1 & 32767;

                int currentPackageLength = 15;
                List<AlarmMessage> alarmMessageList = new ArrayList<>();
                while (currentPackageLength < alarmPackageLength) {
                    GjkzDecoder.Result<String> decodeTitle = decodeString(buffer);
                    numberOfBytesConsumed += decodeTitle.numberOfBytesConsumed;
                    currentPackageLength += decodeTitle.numberOfBytesConsumed;

                    GjkzDecoder.Result<String> decodeContent = decodeString(buffer);
                    numberOfBytesConsumed += decodeContent.numberOfBytesConsumed;
                    currentPackageLength += decodeContent.numberOfBytesConsumed;
                    alarmMessageList.add(new AlarmMessage(decodeTitle.value, decodeContent.value));
                }


                Alarm alarm = new Alarm(time, system, line, station, device, slot, isRecovery, code, alarmMessageList);
                alarmList.add(alarm);
            }
        }

        GjkzAlarmSyncRespPayload gjkzAlarmSyncRespPayload = new GjkzAlarmSyncRespPayload(alarmList);
        return new GjkzDecoder.Result(gjkzAlarmSyncRespPayload, numberOfBytesConsumed);
    }

    private static Result<GjkzAlarmReqPayload> decodeAlarmReqPayload(ByteBuf buffer, int bytesRemainingInVariablePart, GjkzAlarmReqVariableHeader variableHeader) {
        int numberOfBytesConsumed = 0;
        int payloadLength = bytesRemainingInVariablePart;
        List<AlarmMessage> alarmMessageList = new ArrayList<>();
        while (payloadLength > 0) {

            GjkzDecoder.Result<String> decodeTitle = decodeString(buffer);
            payloadLength -= decodeTitle.numberOfBytesConsumed;
            numberOfBytesConsumed += decodeTitle.numberOfBytesConsumed;

            GjkzDecoder.Result<String> decodeContent = decodeString(buffer);
            payloadLength -= decodeContent.numberOfBytesConsumed;
            numberOfBytesConsumed += decodeContent.numberOfBytesConsumed;

            AlarmMessage alarmMessage = new AlarmMessage(decodeTitle.value, decodeContent.value);
            alarmMessageList.add(alarmMessage);
            variableHeader.getAlarm().getAlarmMessageList().add(alarmMessage);
        }


        GjkzAlarmReqPayload gjkzAlarmReqPayload = new GjkzAlarmReqPayload(alarmMessageList);
        return new GjkzDecoder.Result(gjkzAlarmReqPayload, numberOfBytesConsumed);
    }

    private static Result<GjkzControlReqPayload> decodeControlReqPayload(ByteBuf buffer, GjkzControlReqVariableHeader variableHeader) {
        int numberOfBytesConsumed = 0;


        byte[] address = decodeByteArray(buffer);
        numberOfBytesConsumed += 2 + address.length;

        byte[] value = decodeByteArray(buffer);
        numberOfBytesConsumed += 2 + value.length;

        GjkzControlReqPayload gjkzControlReqPayload = new GjkzControlReqPayload(address, value);
        return new GjkzDecoder.Result(gjkzControlReqPayload, numberOfBytesConsumed);
    }

    private static Result<GjkzCResultReqPayload> decodeCResultReqPayload(ByteBuf buffer, GjkzCResultReqVariableHeader variableHeader) {
        int numberOfBytesConsumed = 0;


        int result = buffer.readUnsignedByte();
        ++numberOfBytesConsumed;


        GjkzCResultReqPayload gjkzCResultReqPayload = new GjkzCResultReqPayload(result);
        return new GjkzDecoder.Result(gjkzCResultReqPayload, numberOfBytesConsumed);
    }

    private static Result<GjkzReadReqPayload> decodeReadReqPayload(ByteBuf buffer, GjkzReadReqVariableHeader variableHeader) {
        int numberOfBytesConsumed = 0;


        byte[] address = decodeByteArray(buffer);
        numberOfBytesConsumed += 2 + address.length;

        GjkzReadReqPayload gjkzReadReqPayload = new GjkzReadReqPayload(address);
        return new GjkzDecoder.Result(gjkzReadReqPayload, numberOfBytesConsumed);
    }

    private static Result<GjkzReadRespPayload> decodeReadRespPayload(ByteBuf buffer, GjkzReadRespVariableHeader variableHeader) {
        int numberOfBytesConsumed = 0;


        byte[] value = decodeByteArray(buffer);
        numberOfBytesConsumed += 2 + value.length;

        GjkzReadRespPayload gjkzReadRespPayload = new GjkzReadRespPayload(value);
        return new GjkzDecoder.Result(gjkzReadRespPayload, numberOfBytesConsumed);
    }

    private static GjkzDecoder.Result<String> decodeString(ByteBuf buffer) {
        return decodeString(buffer, 0, 2147483647);
    }

    private static GjkzDecoder.Result<String> decodeString(ByteBuf buffer, int minBytes, int maxBytes) {
        int size = decodeMsbLsb(buffer);
        int numberOfBytesConsumed = 2;
        if (size >= minBytes && size <= maxBytes) {
            String s = buffer.toString(buffer.readerIndex(), size, CharsetUtil.UTF_8);
            buffer.skipBytes(size);
            numberOfBytesConsumed = numberOfBytesConsumed + size;
            return new GjkzDecoder.Result(s, numberOfBytesConsumed);
        } else {
            buffer.skipBytes(size);
            numberOfBytesConsumed = numberOfBytesConsumed + size;
            return new GjkzDecoder.Result((Object) null, numberOfBytesConsumed);
        }
    }

    private static int decodeMsbLsb(ByteBuf buffer) {
        int min = 0;
        int max = '\uffff';
        short msbSize = buffer.readUnsignedByte();
        short lsbSize = buffer.readUnsignedByte();
        int result = msbSize << 8 | lsbSize;
        if (result < min || result > max) {
            result = -1;
        }

        return result;
    }

    private static int decodeYear(ByteBuf buffer) {
        int year;
        int year1 = buffer.readUnsignedByte();
        int year2 = buffer.readUnsignedByte();
        int result = year1 << 8 | year2;
        Calendar calendar = Calendar.getInstance();
        if (Math.abs(result - calendar.get(Calendar.YEAR)) > 1000) {
            String sYear1 = String.valueOf(year1);
            String sYear2 = String.valueOf(year2);
            if (sYear1.length() == 1) {
                sYear1 = "0" + sYear1;
            }
            if (sYear2.length() == 1) {
                sYear2 = "0" + sYear2;
            }
            year = Integer.parseInt(sYear1 + sYear2);
        } else {
            year = result;
        }
        return year;
    }

    private static byte[] decodeByteArray(ByteBuf buffer) {
        int size = decodeMsbLsb(buffer);
        byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        return bytes;
    }

    private static final class Result<T> {
        private final T value;
        private final int numberOfBytesConsumed;

        Result(T value, int numberOfBytesConsumed) {
            this.value = value;
            this.numberOfBytesConsumed = numberOfBytesConsumed;
        }
    }

    static enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD,
        BAD_MESSAGE;

        private DecoderState() {
        }
    }
}