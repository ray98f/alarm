package com.zte.xml.task;

import com.zte.xml.constant.AlarmRequestConstant;
import com.zte.xml.constant.FunctionRequestConstant;
import com.zte.xml.constant.PropertyRequestConstant;
import com.zte.xml.constant.SoapConstant;
import com.zte.xml.util.SoapXmlSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Ray
 */
@Component
@PropertySource("classpath:/application.yml")
@Slf4j
public class SoapSendTask {

    @Scheduled(cron = "${soap-sender.alarm}")
    public void alarmRetrieval() {
        log.info("告警模块：getActiveAlarms查询所有的满足查询条件的当前告警");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.ALARM_RETRIEVAL,
                AlarmRequestConstant.ALARM_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.function}")
    public void performanceManagementRetrieval() {
        log.info("性能模块：getAllCurrentPerformanceMonitoringData获取所有满足查询条件的当前性能");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.PERFORMANCE_MANAGEMENT_RETRIEVAL,
                FunctionRequestConstant.PERFORMANCE_MANAGEMENT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void managementDomainRetrieval() {
        log.info("资产模块：getAllManagementDomains查询所有的管理域(MD)");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.MANAGEMENT_DOMAIN_RETRIEVAL,
                PropertyRequestConstant.MANAGEMENT_DOMAIN_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void managedElementRetrieval() {
        log.info("资产模块：getAllManagedElements查询指定管理域或者多层子网中的所有网元信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.MANAGED_ELEMENT_RETRIEVAL,
                PropertyRequestConstant.MANAGED_ELEMENT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void terminationPointRetrieval() {
        log.info("资产模块：getAllPhysicalTerminationPoints查询指定网元的所有PTP及FTP信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.TERMINATION_POINT_RETRIEVAL,
                PropertyRequestConstant.TERMINATION_POINT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void equipmentInventoryRetrieval() {
        log.info("资产模块：getAllEquipment查询指定网元或者设备容器包含的所有设备及设备容器信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.EQUIPMENT_INVENTORY_RETRIEVAL,
                PropertyRequestConstant.EQUIPMENT_INVENTORY_RETRIEVAL_XML);
    }


}
