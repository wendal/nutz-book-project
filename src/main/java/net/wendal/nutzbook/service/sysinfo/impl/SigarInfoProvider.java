package net.wendal.nutzbook.service.sysinfo.impl;

//import java.util.ArrayList;
//import java.util.List;
//
//import org.hyperic.sigar.Sigar;
//import org.hyperic.sigar.SigarException;
//import org.nutz.ioc.loader.annotation.IocBean;
//import org.nutz.lang.util.NutMap;
//import org.nutz.sigar.gather.CPUGather;
//import org.nutz.sigar.gather.MemoryGather;

//@IocBean
//public class SigarInfoProvider extends AbstractSysInfoProvider {
//    
//    Sigar sigar = new Sigar();
//
//    public String name() {
//        return "操作系统状态";
//    }
//
//    public String description() {
//        return "通过Sigar获取的系统状态";
//    }
//
//    @Override
//    public List<NutMap> fetch() {
//        List<NutMap> re = new ArrayList<>();
//        NutMap map;
//        
//        try {
//            CPUGather cpu = CPUGather.gather(sigar);
//            map = new NutMap();
//            map.put("name", "CPU信息");
//            map.put("value", cpu.getInfo().getVendor() + cpu.getInfo().getModel());
//            re.add(map);
//
//
//            map = new NutMap();
//            map.put("name", "CPU占用率");
//            map.put("value", cpu.getPerc().getCombined() * 100);
//            re.add(map);
//
//            MemoryGather memory = MemoryGather.gather(sigar);
//            map = new NutMap();
//            map.put("name", "总内存");
//            map.put("value", memory.getMem().getRam() / 1024/1024 + "mb");
//            re.add(map);
//
//            map = new NutMap();
//            map.put("name", "内存总占用率");
//            map.put("value", (int)memory.getMem().getUsedPercent() + "%");
//            re.add(map);
//            
//            //map = new NutMap();
//            //map.put("name", "JVM内存占用率");
//            //map.put("value", (int)memory.getJvm().getUsedPercent() + "%");
//            //re.add(map);
//            
//            //map = new NutMap();
//            //map.put("name", "交换分区使用率");
//            //if (memory.getSwap().getTotal() == 0) {
//            //    map.put("value", "无");
//            //} else {
//            //    map.put("value", memory.getSwap().getUsed() * 100 / memory.getSwap().getTotal() + "%");
//            //}
//            //re.add(map);
//
//            //data.put("disk", DISKGather.gather(sigar));
//
//            //data.put("network", NetInterfaceGather.gather(sigar));
//
//            //data.put("system", OSGather.init(sigar));
//        }
//        catch (Exception e) {
//            // 缺库文件 https://support.hyperic.com/display/SIGAR/Home
//            e.printStackTrace();
//        }
//        return re;
//    }
//
//}
