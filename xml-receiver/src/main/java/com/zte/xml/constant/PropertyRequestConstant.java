package com.zte.xml.constant;

public class PropertyRequestConstant {

    public static String MANAGEMENT_DOMAIN_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/mri/xsd/mdr/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getAllManagementDomains</v1:activityName>\n" +
            "         <v1:msgName>getAllManagementDomainsRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:communicationPattern>SimpleResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllManagementDomainsRequest/>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    public static String MANAGED_ELEMENT_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/mri/xsd/mer/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getAllManagedElements</v1:activityName>\n" +
            "         <v1:msgName>getAllManagedElementsRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:communicationPattern>SimpleResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllManagedElementsRequest>\n" +
            "         <v11:mdOrMlsnRef>\n" +
            "            <v12:rdn>\n" +
            "               <v12:type>MD</v12:type>\n" +
            "               <v12:value>ZTE/UME(BN)</v12:value>\n" +
            "            </v12:rdn>\n" +
            "         </v11:mdOrMlsnRef>\n" +
            "      </v11:getAllManagedElementsRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    public static String TERMINATION_POINT_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/mri/xsd/tpr/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\" xmlns:v13=\"http://www.tmforum.org/mtop/nrb/xsd/lay/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "        <v1:activityName>getAllPhysicalTerminationPoints</v1:activityName>\n" +
            "        <v1:msgName>getAllPhysicalTerminationPointsRequest</v1:msgName>\n" +
            "        <v1:msgType>REQUEST</v1:msgType>\n" +
            "        <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "        <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "        <v1:communicationPattern>MultipleBatchResponse</v1:communicationPattern>\n" +
            "        <v1:requestedBatchSize>20</v1:requestedBatchSize>\n" +
            "        <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "        <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "      </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllPhysicalTerminationPointsRequest>\n" +
            "         <v11:meRef>\n" +
            "             <v12:rdn>\n" +
            "               <v12:type>MD</v12:type>\n" +
            "               <v12:value>ZTE/UME(BN)</v12:value>\n" +
            "            </v12:rdn>\n" +
            "           <v12:rdn>\n" +
            "               <v12:type>ME</v12:type>\n" +
            "               <v12:value>fa552cdb-60f6-4305-a4a2-829596463237</v12:value>\n" +
            "            </v12:rdn>\n" +
            "         </v11:meRef>\n" +
            "         <v11:tpLayerRateList>\n" +
            "         </v11:tpLayerRateList>\n" +
            "         <v11:connectionLayerRateList>\n" +
            "         </v11:connectionLayerRateList>\n" +
            "      </v11:getAllPhysicalTerminationPointsRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    public static String EQUIPMENT_INVENTORY_RETRIEVAL_XML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:v11=\"http://www.tmforum.org/mtop/mri/xsd/eir/v1\" xmlns:v12=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\">\n" +
            "   <soapenv:Header>\n" +
            "      <v1:header>\n" +
            "         <v1:activityName>getAllEquipment</v1:activityName>\n" +
            "         <v1:msgName>getAllEquipmentRequest</v1:msgName>\n" +
            "         <v1:msgType>REQUEST</v1:msgType>\n" +
            "         <v1:senderURI>/MTOSI/InventoryOS</v1:senderURI>\n" +
            "         <v1:destinationURI>/MTOSI/EmsOS</v1:destinationURI>\n" +
            "         <v1:communicationPattern>MultipleBatchResponse</v1:communicationPattern>\n" +
            "         <v1:communicationStyle>RPC</v1:communicationStyle>\n" +
            "         <v1:security>Api:Wzmtr@123</v1:security>\n" +
            "         <v1:requestedBatchSize>10</v1:requestedBatchSize>" +
            "     </v1:header>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <v11:getAllEquipmentRequest>\n" +
            "         <v11:meOrEhRef>\n" +
            "            <v12:rdn>\n" +
            "               <v12:type>MD</v12:type>\n" +
            "               <v12:value>ZTE/UME(BN)</v12:value>\n" +
            "            </v12:rdn>\n" +
            "            <v12:rdn>\n" +
            "               <v12:type>ME</v12:type>\n" +
            "               <v12:value>fa552cdb-60f6-4305-a4a2-829596463237</v12:value>\n" +
            "            </v12:rdn>\n" +
            "         </v11:meOrEhRef>\n" +
            "      </v11:getAllEquipmentRequest>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

}
