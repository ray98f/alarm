package com.zte.alarm.core.codec.gjkz;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 17:30
 */

final class GjkzCodecUtil {
    private static final char[] TOPIC_WILDCARDS = new char[]{'#', '+'};
    private static final int MIN_CLIENT_ID_LENGTH = 1;
    private static final int MAX_CLIENT_ID_LENGTH = 23;
    static final AttributeKey<GjkzVersion> GJKZ_VERSION_KEY = AttributeKey.valueOf("NETTY_CODEC_GJKZ_VERSION");

    static GjkzVersion getGjkzVersion(ChannelHandlerContext ctx) {
        Attribute<GjkzVersion> attr = ctx.channel().attr(GJKZ_VERSION_KEY);
        GjkzVersion version = (GjkzVersion)attr.get();
        return version == null ? GjkzVersion.GJKZ_1 : version;
    }

    static void setGjkzVersion(ChannelHandlerContext ctx, GjkzVersion version) {
        Attribute<GjkzVersion> attr = ctx.channel().attr(GJKZ_VERSION_KEY);
        attr.set(version);
    }

    static boolean isValidPublishTopicName(String topicName) {
        char[] var1 = TOPIC_WILDCARDS;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            char c = var1[var3];
            if (topicName.indexOf(c) >= 0) {
                return false;
            }
        }

        return true;
    }

    static boolean isValidMessageId(int messageId) {
        return messageId != 0;
    }

    static boolean isValidClientId(String clientId) {
        return clientId != null && clientId.length() >= 1 && clientId.length() <= 23;
    }

    static GjkzFixedHeader validateFixedHeader(ChannelHandlerContext ctx, GjkzFixedHeader gjkzFixedHeader) {
         switch(gjkzFixedHeader.messageType()) {
            default:
                return gjkzFixedHeader;
        }
    }

    static GjkzFixedHeader resetUnusedFields(GjkzFixedHeader gjkzFixedHeader) {
        switch(gjkzFixedHeader.messageType()) {
            default:
                return gjkzFixedHeader;
        }
    }

    private GjkzCodecUtil() {
    }
}
