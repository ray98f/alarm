package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.exception.EncoderException;
import com.zte.alarm.core.pojo.Alarm;
import com.zte.alarm.core.pojo.AlarmMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 17:17
 */
@Slf4j
public class GjkzEncoder extends MessageToMessageEncoder<GjkzMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, GjkzMessage msg, List<Object> out) {
        out.add(doEncode(ctx, msg));
    }

    public static ByteBuf doEncode(Object message) {
        return doEncode(null, (GjkzMessage)message);
    }

    private static ByteBuf doEncode(ChannelHandlerContext ctx, GjkzMessage message) {
        ByteBuf buf = null;
        switch (message.getGjkzFixedHeader().messageType()) {
            case CONNECT:
                buf = encodeConnectMessage(ctx, (GjkzConnectMessage) message);
                break;
            case CONNACK:
                buf = encodeConnAckMessage(ctx, (GjkzConnAckMessage) message);
                break;
            case ALARMSYNCREQ:
                buf = encodeAlarmSyncReqMessage(ctx, (GjkzAlarmSyncReqMessage) message);
                break;
            case ALARMSYNCRESP:
                buf = encodeAlarmSyncRespMessage(ctx, (GjkzAlarmSyncRespMessage) message);
                break;
            case ALARMREQ:
                buf = encodeAlarmReqMessage(ctx, (GjkzAlarmReqMessage) message);
                break;
            case ALARMRESP:
                buf = encodeAlarmRespMessage(ctx, (GjkzAlarmRespMessage) message);
                break;
            case CONTROLREQ:
                buf = encodeControlReqMessage(ctx, (GjkzControlReqMessage) message);
                break;
            case CONTROLRESP:
                buf = encodeControlRespMessage(ctx, (GjkzControlRespMessage) message);
                break;
            case CRESULTREQ:
                buf = encodeCResultReqMessage(ctx, (GjkzCResultReqMessage) message);
                break;
            case CRESULTRESP:
                buf = encodeCResultRespMessage(ctx, (GjkzCResultRespMessage) message);
                break;
            case READREQ:
                buf = encodeReadeReqMessage(ctx, (GjkzReadReqMessage) message);
                break;
            case READRESP:
                buf = encodeReadRespMessage(ctx, (GjkzReadRespMessage) message);
                break;
            case DISCONNECT:
            case PINGREQ:
            case PINGRESP:
                buf = encodeMessageWithOnlySingleByteFixedHeader(ctx, message);
                break;
            default:
                throw new IllegalArgumentException("未知的消息类型: " + message.getGjkzFixedHeader().messageType().value());
        }
        return buf;
    }

    private static ByteBuf encodeAlarmSyncRespMessage(ChannelHandlerContext ctx, GjkzAlarmSyncRespMessage message) {
        int variableHeaderBufferSize = 7;
        int payloadBufferSize = 0;

        int alarmListSize = 0;
        if (message.getPayload().getAlarmList() != null) {
            alarmListSize = message.getPayload().getAlarmList().size();
        }
        if (message.getVariableHeader().getNumberOfAlarmsInThisPacket() != alarmListSize) {
            throw new EncoderException("当前包告警数量与有效载荷中告警数量不一致");
        }

        if (message.getVariableHeader().getNumberOfAlarmsInThisPacket() != 0 && alarmListSize != 0) {
            for (int i = 0; i < message.getPayload().getAlarmList().size(); i++) {
                Alarm alarm = message.getPayload().getAlarmList().get(i);
                int alarmPackageSize = 15;
                if (alarm.getAlarmMessageList() != null) {
                    for (AlarmMessage alarmMessage : alarm.getAlarmMessageList()) {
                        int titleBytes = ByteBufUtil.utf8Bytes(alarmMessage.getTitle());
                        alarmPackageSize += 2 + titleBytes;
                        int contentBytes = ByteBufUtil.utf8Bytes(alarmMessage.getContent());
                        alarmPackageSize += 2 + contentBytes;
                    }
                }
                payloadBufferSize += 3 + alarmPackageSize;

            }
        }


        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);

        buf.writeShort(message.getVariableHeader().getTotalNumberOfAlarms());
        buf.writeByte(message.getVariableHeader().getNumberOfAlarmsInThisPacket());
        buf.writeByte(message.getVariableHeader().getTotalNumberOfPackets());
        buf.writeByte(message.getVariableHeader().getCurrentPackageNumber());
        buf.writeShort(message.getVariableHeader().getMessageIdentifier());

        if (message.getVariableHeader().getNumberOfAlarmsInThisPacket() != 0 && alarmListSize != 0) {
            for (int i = 0; i < message.getPayload().getAlarmList().size(); i++) {
                Alarm alarm = message.getPayload().getAlarmList().get(i);
                int alarmPackageSize = 15;
                if (alarm.getAlarmMessageList() != null) {
                    for (AlarmMessage alarmMessage : alarm.getAlarmMessageList()) {
                        int titleBytes = ByteBufUtil.utf8Bytes(alarmMessage.getTitle());
                        alarmPackageSize += 2 + titleBytes;
                        int contentBytes = ByteBufUtil.utf8Bytes(alarmMessage.getContent());
                        alarmPackageSize += 2 + contentBytes;
                    }
                }

                buf.writeByte(i + 1);
                buf.writeShort(alarmPackageSize);
                buf.writeShort(alarm.getAlarmTime().getYear());
                buf.writeByte(alarm.getAlarmTime().getMonthValue());
                buf.writeByte(alarm.getAlarmTime().getDayOfMonth());
                buf.writeByte(alarm.getAlarmTime().getHour());
                buf.writeByte(alarm.getAlarmTime().getMinute());
                buf.writeByte(alarm.getAlarmTime().getSecond());
                buf.writeByte(alarm.getSystem());
                buf.writeByte(alarm.getLine());
                buf.writeByte(alarm.getStation());
                buf.writeShort(alarm.getDevice());
                buf.writeByte(alarm.getSlot());

                buf.writeShort(getAlarmCode(alarm.isRecovery(), alarm.getAlarmCode()));

                if (alarm.getAlarmMessageList() != null) {
                    for (AlarmMessage alarmMessage : alarm.getAlarmMessageList()) {
                        int titleBytes = ByteBufUtil.utf8Bytes(alarmMessage.getTitle());
                        writeExactUTF8String(buf, alarmMessage.getTitle(), titleBytes);
                        int contentBytes = ByteBufUtil.utf8Bytes(alarmMessage.getContent());
                        writeExactUTF8String(buf, alarmMessage.getContent(), contentBytes);
                    }
                }

            }
        }
        return buf;
    }

    private static ByteBuf encodeAlarmReqMessage(ChannelHandlerContext ctx, GjkzAlarmReqMessage message) {

        int variableHeaderBufferSize = 17;
        int payloadBufferSize = 0;

        if (message.getPayload().getAlarmMessageList() != null) {
            for (AlarmMessage alarmMessage : message.getPayload().getAlarmMessageList()) {
                int titleBytes = ByteBufUtil.utf8Bytes(alarmMessage.getTitle());
                payloadBufferSize += 2 + titleBytes;
                int contentBytes = ByteBufUtil.utf8Bytes(alarmMessage.getContent());
                payloadBufferSize += 2 + contentBytes;
            }
        }

        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }
        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);


        Alarm alarm = message.getVariableHeader().getAlarm();

        buf.writeShort(alarm.getAlarmTime().getYear());
        buf.writeByte(alarm.getAlarmTime().getMonthValue());
        buf.writeByte(alarm.getAlarmTime().getDayOfMonth());
        buf.writeByte(alarm.getAlarmTime().getHour());
        buf.writeByte(alarm.getAlarmTime().getMinute());
        buf.writeByte(alarm.getAlarmTime().getSecond());
        buf.writeByte(alarm.getSystem());
        buf.writeByte(alarm.getLine());
        buf.writeByte(alarm.getStation());
        buf.writeShort(alarm.getDevice());
        buf.writeByte(alarm.getSlot());
        buf.writeShort(getAlarmCode(alarm.isRecovery(), alarm.getAlarmCode()));

        buf.writeShort(message.getVariableHeader().getMessageId());

        if (message.getPayload().getAlarmMessageList() != null) {
            for (AlarmMessage alarmMessage : message.getPayload().getAlarmMessageList()) {
                int titleBytes = ByteBufUtil.utf8Bytes(alarmMessage.getTitle());
                writeExactUTF8String(buf, alarmMessage.getTitle(), titleBytes);
                int contentBytes = ByteBufUtil.utf8Bytes(alarmMessage.getContent());
                writeExactUTF8String(buf, alarmMessage.getContent(), contentBytes);
            }
        }

        return buf;
    }

    private static ByteBuf encodeControlRespMessage(ChannelHandlerContext ctx, GjkzControlRespMessage message) {
        int bufferSize = 4;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());

        writeVariableLengthInt(buf, 2);

        buf.writeShort(message.getVariableHeader().getMessageId());

        return buf;
    }

    private static ByteBuf encodeCResultReqMessage(ChannelHandlerContext ctx, GjkzCResultReqMessage message) {
        int bufferSize = 5;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());

        writeVariableLengthInt(buf, 3);

        buf.writeShort(message.getVariableHeader().getMessageId());
        buf.writeByte(message.getPayload().getResult());

        return buf;
    }

    private static ByteBuf encodeReadRespMessage(ChannelHandlerContext ctx, GjkzReadRespMessage message) {

        int variableHeaderBufferSize = 2;
        int payloadBufferSize = 2 + message.getPayload().getValue().length;

        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);

        buf.writeShort(message.getVariableHeader().getMessageId());

        buf.writeShort(message.getPayload().getValue().length);
        buf.writeBytes(message.getPayload().getValue());
        return buf;
    }

    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(ChannelHandlerContext ctx, GjkzMessage message) {
        GjkzFixedHeader gjkzFixedHeader = message.getGjkzFixedHeader();
        int bufferSize = 2;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(getFixedHeaderByte1(gjkzFixedHeader));
        buf.writeByte(0);
        return buf;
    }

    private static ByteBuf encodeConnectMessage(ChannelHandlerContext ctx, GjkzConnectMessage message) {


        GjkzConnectVariableHeader variableHeader = message.getVariableHeader();
        GjkzConnectPayload payload = message.getPayload();
        GjkzVersion gjkzVersion = GjkzVersion.fromProtocolNameAndLevel(variableHeader.getName(), (byte) variableHeader.getVersion());
        byte[] protocolNameBytes = gjkzVersion.protocolNameBytes();

        String clientIdentifier = payload.getClientIdentifier();
        int clientIdentifierBytes = ByteBufUtil.utf8Bytes(clientIdentifier);

        int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4;
        int payloadBufferSize = 2 + clientIdentifierBytes;

        if (!variableHeader.isHasUserName() && variableHeader.isHasPassword()) {
            throw new EncoderException("密码标识为1时,用户名标识也应为1.");
        }

        if (variableHeader.isHasUserName()) {
            String userName = payload.getUserName();
            int userNameBytes = nullableUtf8Bytes(userName);
            payloadBufferSize += 2 + userNameBytes;
        }

        if (variableHeader.isHasPassword()) {
            byte[] password = payload.getPassword();
            byte[] passwordBytes = password != null ? password : EmptyArrays.EMPTY_BYTES;
            payloadBufferSize += 2 + passwordBytes.length;
        }

        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);

        buf.writeShort(protocolNameBytes.length);
        buf.writeBytes(protocolNameBytes);
        buf.writeByte(variableHeader.getVersion());
        buf.writeByte(getConnVariableHeaderFlag(variableHeader));
        buf.writeShort(variableHeader.getKeepAliveTimeSeconds());

        writeExactUTF8String(buf, clientIdentifier, clientIdentifierBytes);
        if (variableHeader.isHasUserName()) {
            String userName = payload.getUserName();
            int userNameBytes = nullableUtf8Bytes(userName);
            writeExactUTF8String(buf, userName, userNameBytes);
        }
        if (variableHeader.isHasPassword()) {
            byte[] password = payload.getPassword();
            byte[] passwordBytes = password != null ? password : EmptyArrays.EMPTY_BYTES;
            buf.writeShort(passwordBytes.length);
            buf.writeBytes(passwordBytes, 0, passwordBytes.length);
        }

        return buf;
    }

    private static ByteBuf encodeConnAckMessage(ChannelHandlerContext ctx, GjkzConnAckMessage message) {
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(3);
        } else {
            buf = ctx.alloc().buffer(3);
        }
        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, 1);
        buf.writeByte(message.getVariableHeader().getConnectReturnCode().byteValue());
        return buf;
    }

    private static ByteBuf encodeAlarmSyncReqMessage(ChannelHandlerContext ctx, GjkzAlarmSyncReqMessage message) {
        int bufferSize = 2;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, 0);
        return buf;
    }

    private static ByteBuf encodeAlarmRespMessage(ChannelHandlerContext ctx, GjkzAlarmRespMessage message) {
        int bufferSize = 4;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }
        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, 2);
        buf.writeShort(message.getVariableHeader().getMessageId());
        return buf;
    }

    private static ByteBuf encodeControlReqMessage(ChannelHandlerContext ctx, GjkzControlReqMessage message) {

        int variableHeaderBufferSize = 7;
        int payloadBufferSize = 2 + message.getPayload().getAddress().length + 2 + message.getPayload().getValue().length;

        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);

        buf.writeByte(message.getVariableHeader().getVersion());
        buf.writeShort(message.getVariableHeader().getDevice());
        buf.writeShort(message.getVariableHeader().getFunctionCode());
        buf.writeShort(message.getVariableHeader().getMessageId());

        buf.writeShort(message.getPayload().getAddress().length);
        buf.writeBytes(message.getPayload().getAddress());
        buf.writeShort(message.getPayload().getValue().length);
        buf.writeBytes(message.getPayload().getValue());
        return buf;
    }

    private static ByteBuf encodeCResultRespMessage(ChannelHandlerContext ctx, GjkzCResultRespMessage message) {
        int bufferSize = 4;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());

        writeVariableLengthInt(buf, 2);

        buf.writeShort(message.getVariableHeader().getMessageId());

        return buf;
    }

    private static ByteBuf encodeReadeReqMessage(ChannelHandlerContext ctx, GjkzReadReqMessage message) {

        int variableHeaderBufferSize = 7;
        int payloadBufferSize = 2 + message.getPayload().getAddress().length;

        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

        int bufferSize = fixedHeaderBufferSize + variablePartSize;
        ByteBuf buf;
        if (ctx == null) {
            buf = Unpooled.buffer(bufferSize);
        } else {
            buf = ctx.alloc().buffer(bufferSize);
        }

        buf.writeByte(message.getGjkzFixedHeader().messageType().value());
        writeVariableLengthInt(buf, variablePartSize);

        buf.writeByte(message.getVariableHeader().getVersion());
        buf.writeShort(message.getVariableHeader().getDevice());
        buf.writeShort(message.getVariableHeader().getFunctionCode());
        buf.writeShort(message.getVariableHeader().getMessageId());

        buf.writeShort(message.getPayload().getAddress().length);
        buf.writeBytes(message.getPayload().getAddress());
        return buf;
    }

    private static int nullableUtf8Bytes(String s) {
        return s == null ? 0 : ByteBufUtil.utf8Bytes(s);
    }

    private static int getVariableLengthInt(int num) {
        int count = 0;

        do {
            num /= 128;
            ++count;
        } while (num > 0);

        return count;
    }

    private static int getFixedHeaderByte1(GjkzFixedHeader header) {
        return header.messageType().value();
    }

    private static void writeVariableLengthInt(ByteBuf buf, int num) {
        do {
            int digit = num % 128;
            num /= 128;
            if (num > 0) {
                digit |= 128;
            }

            buf.writeByte(digit);
        } while (num > 0);
    }

    private static int getAlarmCode(boolean isRecovery, int alarmCode) {
        if (isRecovery) {
            alarmCode |= 32768;
        }
        return alarmCode;
    }

    private static int getConnVariableHeaderFlag(GjkzConnectVariableHeader variableHeader) {
        int flagByte = 0;
        if (variableHeader.isHasUserName()) {
            flagByte |= 128;
        }

        if (variableHeader.isHasPassword()) {
            flagByte |= 64;
        }

        return flagByte;
    }

    private static void writeExactUTF8String(ByteBuf buf, String s, int utf8Length) {
        buf.ensureWritable(utf8Length + 2);
        buf.writeShort(utf8Length);
        if (utf8Length > 0) {
            int writtenUtf8Length = ByteBufUtil.reserveAndWriteUtf8(buf, s, utf8Length);

            assert writtenUtf8Length == utf8Length;
        }
    }

    public static ByteBuf encodeAlarmSyncRespMessage(GjkzAlarmSyncRespMessage message) {
        return encodeAlarmSyncRespMessage(null, message);
    }

    public static ByteBuf encodeAlarmReqMessage(GjkzAlarmReqMessage message) {
        return encodeAlarmReqMessage(null, message);
    }

    public static ByteBuf encodeControlRespMessage(GjkzControlRespMessage message) {
        return encodeControlRespMessage(null, message);
    }

    public static ByteBuf encodeCResultReqMessage(GjkzCResultReqMessage message) {
        return encodeCResultReqMessage(null, message);
    }

    public static ByteBuf encodeReadRespMessage(GjkzReadRespMessage message) {
        return encodeReadRespMessage(null, message);
    }

    public static ByteBuf encodeDisconnectMessage(GjkzMessage message) {
        return encodeMessageWithOnlySingleByteFixedHeader(null, message);
    }

    public static ByteBuf encodePingReqMessage(GjkzMessage message) {
        return encodeMessageWithOnlySingleByteFixedHeader(null, message);
    }

    public static ByteBuf encodePingRespMessage(GjkzMessage message) {
        return encodeMessageWithOnlySingleByteFixedHeader(null, message);
    }

    public static ByteBuf encodeConnectMessage(GjkzConnectMessage message) {
        return encodeConnectMessage(null, message);
    }

    public static ByteBuf encodeConnAckMessage(GjkzConnAckMessage message) {
        return encodeConnAckMessage(null, message);
    }

    public static ByteBuf encodeAlarmSyncReqMessage(GjkzAlarmSyncReqMessage message) {
        return encodeAlarmSyncReqMessage(null, message);
    }

    public static ByteBuf encodeAlarmRespMessage(GjkzAlarmRespMessage message) {
        return encodeAlarmRespMessage(null, message);
    }

    public static ByteBuf encodeControlReqMessage(GjkzControlReqMessage message) {
        return encodeControlReqMessage(null, message);
    }

    public static ByteBuf encodeCResultRespMessage(GjkzCResultRespMessage message) {
        return encodeCResultRespMessage(null, message);
    }

    public static ByteBuf encodeReadeReqMessage(GjkzReadReqMessage message) {
        return encodeReadeReqMessage(null, message);
    }
}
