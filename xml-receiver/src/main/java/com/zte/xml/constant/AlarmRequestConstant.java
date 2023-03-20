package com.zte.xml.constant;

public class AlarmRequestConstant {

    public static String ALARM_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/rtm/xsd/ar/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\" xmlns:v13=\"http://www.tmforum.org/mtop/nra/xsd/com/v1\" xmlns:v14=\"http://www.tmforum.org/mtop/nra/xsd/prc/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getActiveAlarms</v1:activityName>\n" +
            "         <v1:msgName>getActiveAlarmsRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:communicationPattern>SimpleResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getActiveAlarmsRequest>\n" +
            "         <v11:filter>\n" +
            "            <v11:acknowledgeIndication>UNACKNOWLEDGED</v11:acknowledgeIndication>\n" +
            "         </v11:filter>\n" +
            "      </v11:getActiveAlarmsRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

}
