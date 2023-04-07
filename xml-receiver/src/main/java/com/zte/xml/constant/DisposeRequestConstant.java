package com.zte.xml.constant;

public class DisposeRequestConstant {

    public static String CONNECTION_RETRIEVAL_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/mri/xsd/conr/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\" xmlns:v13=\"http://www.tmforum.org/mtop/nrb/xsd/lay/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getAllCrossConnections</v1:activityName>\n" +
            "         <v1:msgName>getAllCrossConnectionsRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:communicationPattern>MultipleBatchResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:requestedBatchSize>1</v1:requestedBatchSize>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllCrossConnectionsRequest>\n" +
            "         <v11:meRef>\n" +
            "            <v12:rdn>\n" +
            "               <v12:type>MD</v12:type>\n" +
            "               <v12:value>ZTE/UME(BN)</v12:value>\n" +
            "            </v12:rdn>\n" +
            "            <v12:rdn>\n" +
            "               <v12:type>ME</v12:type>\n" +
            "               <v12:value>fa552cdb-60f6-4305-a4a2-829596463237</v12:value>\n" +
            "            </v12:rdn>\n" +
            "         </v11:meRef>\n" +
            "         <v11:connectionRateList>\n" +
            "            <!-- <v13:layerRate extension=\"\">LR_Optical_Channel</v13:layerRate> -->\n" +
            "            <!-- <v13:layerRate extension=\"\">LR_OCH_Data_Unit_2</v13:layerRate> -->\n" +
            "            <!-- <v13:layerRate extension=\"\">LR_OCH_Data_Unit_0</v13:layerRate> -->\n" +
            "         </v11:connectionRateList>\n" +
            "      </v11:getAllCrossConnectionsRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

}
