package com.zte.xml.constant;

public class FunctionRequestConstant {

    public static String PERFORMANCE_MANAGEMENT_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/rpm/xsd/pmr/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/nra/xsd/pmtgt/v1\" xmlns:v13=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\" xmlns:v14=\"http://www.tmforum.org/mtop/nrb/xsd/lay/v1\" xmlns:v15=\"http://www.tmforum.org/mtop/nra/xsd/pm/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getAllCurrentPerformanceMonitoringData</v1:activityName>\n" +
            "         <v1:msgName>getAllCurrentPerformanceMonitoringDataRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "         <v1:communicationPattern>MultipleBatchResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:requestedBatchSize>1</v1:requestedBatchSize>" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllCurrentPerformanceMonitoringDataRequest>\n" +
            "         <v11:pmObjectSelectList>\n" +
            "            <v12:pmObjectSelect>\n" +
            "               <v12:name>\n" +
            "                  <v13:rdn>\n" +
            "                     <v13:type>MD</v13:type>\n" +
            "                     <v13:value>ZTE/UME(BN)</v13:value>\n" +
            "                  </v13:rdn>\n" +
            "                  <v13:rdn>\n" +
            "                     <v13:type>ME</v13:type>\n" +
            "                     <v13:value>fa552cdb-60f6-4305-a4a2-829596463237</v13:value>\n" +
            "                  </v13:rdn>\n" +
            "                  <v13:rdn>\n" +
            "                     <v13:type>PTP</v13:type>\n" +
            "                     <v13:value>/rack=0/shelf=1/slot=3/type=Eth_U/port=3</v13:value>\n" +
            "                  </v13:rdn>\n" +
            "               </v12:name>\n" +
            "               <v12:layerRateList />\n" +
            "               <v12:pmLocationList />\n" +
            "               <v12:granularityList />\n" +
            "            </v12:pmObjectSelect>\n" +
            "         </v11:pmObjectSelectList>\n" +
            "         <v11:pmParameterList/>\n" +
            "      </v11:getAllCurrentPerformanceMonitoringDataRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

}
