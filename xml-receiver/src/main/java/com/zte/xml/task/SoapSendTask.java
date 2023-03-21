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
        log.info("获取告警数据上报：");
        log.info("getActiveAlarms查询所有的满足查询条件的当前告警");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.ALARM_RETRIEVAL,
                AlarmRequestConstant.ALARM_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.function}")
    public void performanceManagementRetrieval() {
        log.info("获取性能数据上报：");
        log.info("getAllCurrentPerformanceMonitoringData获取所有满足查询条件的当前性能");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.PERFORMANCE_MANAGEMENT_RETRIEVAL,
                FunctionRequestConstant.PERFORMANCE_MANAGEMENT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void managementDomainRetrieval() {
        log.info("获取资产数据上报：");
        log.info("getAllManagementDomains查询所有的管理域(MD)");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.MANAGEMENT_DOMAIN_RETRIEVAL,
                PropertyRequestConstant.MANAGEMENT_DOMAIN_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void managedElementRetrieval() {
        log.info("获取资产数据上报：");
        log.info("getAllManagedElements查询指定管理域或者多层子网中的所有网元信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.MANAGED_ELEMENT_RETRIEVAL,
                PropertyRequestConstant.MANAGED_ELEMENT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void terminationPointRetrieval() {
        log.info("获取资产数据上报：");
        log.info("getAllPhysicalTerminationPoints查询指定网元的所有PTP及FTP信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.TERMINATION_POINT_RETRIEVAL,
                PropertyRequestConstant.TERMINATION_POINT_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.property}")
    public void equipmentInventoryRetrieval() {
        log.info("获取资产数据上报：");
        log.info("getAllEquipment查询指定网元或者设备容器包含的所有设备及设备容器信息");
        SoapXmlSender.sendSoapXml(SoapConstant.URL
                        + SoapConstant.BASE_PATH
                        + SoapConstant.EQUIPMENT_INVENTORY_RETRIEVAL,
                PropertyRequestConstant.EQUIPMENT_INVENTORY_RETRIEVAL_XML);
    }

    @Scheduled(cron = "${soap-sender.dispose}")
    public void dispose() {
        log.info("获取配置数据上报：");
        log.info("setCommonAttributes设置指定对象的公共属性");
        log.info("<ns4:setCommonAttributes xmlns=\"http://www.tmforum.org/mtop/fmw/xsd/hdr/v1\" xmlns:ns2=\"http://www.tmforum.org/mtop/fmw/xsd/gen/v1\" xmlns:ns3=\"http://www.tmforum.org/mtop/fmw/xsd/nam/v1\" xmlns:ns4=\"http://www.tmforum.org/mtop/mri/xsd/mdr/v1\" xmlns:ns5=\"http://www.tmforum.org/mtop/fmw/xsd/coi/v1\" xmlns:ns6=\"http://www.tmforum.org/mtop/mri/xsd/mlsnr/v1\" xmlns:ns7=\"http://www.tmforum.org/mtop/nrb/xsd/cri/v1\" xmlns:ns8=\"http://www.tmforum.org/mtop/nra/xsd/tcapp/v1\" xmlns:ns9=\"http://www.tmforum.org/mtop/nra/xsd/tcapar/v1\" xmlns:ns10=\"http://www.tmforum.org/mtop/nra/xsd/pm/v1\" xmlns:ns11=\"http://www.tmforum.org/mtop/nrb/xsd/lay/v1\" xmlns:ns12=\"http://www.tmforum.org/mtop/nrf/xsd/cp/v1\" xmlns:ns13=\"http://www.tmforum.org/mtop/nrf/xsd/com/v1\" xmlns:ns14=\"http://www.tmforum.org/mtop/fmw/xsd/cosd/v1\" xmlns:ns15=\"http://www.tmforum.org/mtop/nrb/xsd/crmd/v1\" xmlns:ns16=\"http://www.tmforum.org/mtop/fmw/xsd/cocd/v1\" xmlns:ns17=\"http://www.tmforum.org/mtop/nrb/xsd/crcd/v1\" xmlns:ns18=\"http://www.tmforum.org/mtop/rp/xsd/fdc/v1\" xmlns:ns19=\"http://www.tmforum.org/mtop/nrb/xsd/lp/v1\" xmlns:ns20=\"http://www.tmforum.org/mtop/nrf/xsd/tpdata/v1\" xmlns:ns21=\"http://www.tmforum.org/mtop/nrf/xsd/cc/v1\" xmlns:ns22=\"http://www.tmforum.org/mtop/nrf/xsd/mfd/v1\" xmlns:ns23=\"http://www.tmforum.org/mtop/nrf/xsd/tppool/v1\" xmlns:ns24=\"http://www.tmforum.org/mtop/nrf/xsd/tl/v1\" xmlns:ns25=\"http://www.tmforum.org/mtop/nrf/xsd/gtp/v1\" xmlns:ns26=\"http://www.tmforum.org/mtop/nrf/xsd/mlsn/v1\" xmlns:ns27=\"http://www.tmforum.org/mtop/nrf/xsd/route/v1\" xmlns:ns28=\"http://www.tmforum.org/mtop/nra/xsd/asap/v1\" xmlns:ns29=\"http://www.tmforum.org/mtop/nra/xsd/prc/v1\" xmlns:ns30=\"http://www.tmforum.org/mtop/nra/xsd/asa/v1\" xmlns:ns31=\"http://www.tmforum.org/mtop/nrf/xsd/sor/v1\" xmlns:ns32=\"http://www.tmforum.org/mtop/nrf/xsd/tmd/v1\" xmlns:ns33=\"http://www.tmforum.org/mtop/nra/xsd/pmp/v1\" xmlns:ns34=\"http://www.tmforum.org/mtop/nra/xsd/pmth/v1\" xmlns:ns35=\"http://www.tmforum.org/mtop/nrb/xsd/itu/v1\" xmlns:ns36=\"http://www.tmforum.org/mtop/nrf/xsd/eq/v1\" xmlns:ns37=\"http://www.tmforum.org/mtop/nrf/xsd/fdfr/v1\" xmlns:ns38=\"http://www.tmforum.org/mtop/nrf/xsd/fd/v1\" xmlns:ns39=\"http://www.tmforum.org/mtop/nrf/xsd/tcp/v1\" xmlns:ns40=\"http://www.tmforum.org/mtop/nrf/xsd/ctp/v1\" xmlns:ns41=\"http://www.tmforum.org/mtop/nrf/xsd/ftp/v1\" xmlns:ns42=\"http://www.tmforum.org/mtop/nrf/xsd/ptp/v1\" xmlns:ns43=\"http://www.tmforum.org/mtop/nrf/xsd/snc/v1\" xmlns:ns44=\"http://www.tmforum.org/mtop/nrf/xsd/ns/v1\" xmlns:ns45=\"http://www.tmforum.org/mtop/nrf/xsd/me/v1\" xmlns:ns46=\"http://www.tmforum.org/mtop/nrf/xsd/eh/v1\" xmlns:ns47=\"http://www.tmforum.org/mtop/nra/xsd/epg/v1\" xmlns:ns48=\"http://www.tmforum.org/mtop/nra/xsd/pgp/v1\" xmlns:ns49=\"http://www.tmforum.org/mtop/nra/xsd/com/v1\" xmlns:ns50=\"http://www.tmforum.org/mtop/nrf/xsd/os/v1\" xmlns:ns51=\"http://www.tmforum.org/mtop/nra/xsd/pg/v1\" xmlns:ns52=\"http://www.tmforum.org/mtop/fmw/xsd/msg/v1\" xmlns:ns53=\"http://www.tmforum.org/mtop/mri/xsd/clsrc/tpr/v1\" xmlns:ns54=\"http://www.tmforum.org/mtop/rp/xsd/clsrc/v1\" xmlns:ns55=\"http://www.tmforum.org/mtop/fmw/xsd/md/v1\" xmlns:ns56=\"http://www.tmforum.org/mtop/fmw/xsd/vob/v1\"><ns4:result>success<ns4:result></ns4:setCommonAttributes>");
    }


}
