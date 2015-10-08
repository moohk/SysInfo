package com.coe.lce.system.internal;

import java.io.File;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coe.lce.system.SystemStatus;

public class SystemStatusImpl implements SystemStatus {

	private Logger logger = LoggerFactory.getLogger(SystemStatusImpl.class);

	private static Sigar sigar;

	public SystemStatusImpl(Sigar sigar) {
		//super(SystemStatus.class);
		this.sigar = sigar;
	}

	/* Total number of processors or cores available to the JVM */
	public int getProcessors() {
		System.out.println("Available processors (cores): "
				+ Runtime.getRuntime().availableProcessors());
		return Runtime.getRuntime().availableProcessors();
	}

	/* Total amount of free memory available to the JVM */
	public Long getJvmFreeMemory() {
		System.out.println("Free memory (bytes): "
				+ Runtime.getRuntime().freeMemory());
		return Runtime.getRuntime().freeMemory();
	}

	/* This will return Long.MAX_VALUE if there is no preset limit */
	public Long getJvmMaxMemory() {
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (bytes): "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
		return maxMemory;
	}

	/* Total memory currently available to the JVM */
	public Long getJvmTotalMemory() {
		System.out.println("Total memory available to JVM (bytes): "
				+ Runtime.getRuntime().totalMemory());
		return Runtime.getRuntime().totalMemory();
	}

	/* Total memory info for JVM */
	public HashMap<String, Object> getJvmMemoryInfoMB() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("free", Runtime.getRuntime().freeMemory() / 1024 / 1024);
		map.put("max", Runtime.getRuntime().maxMemory() / 1024 / 1024);
		map.put("total", Runtime.getRuntime().totalMemory() / 1024 / 1024);

		// List<HashMap<String, Object>> list = new ArrayList<HashMap<String,
		// Object>>();
		// list.add(map);
		return map;
	}

	public HashMap<String, Object> getSystemMemoryInfo() {

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Mem mem = null;
		try {
			mem = sigar.getMem();
		} catch (SigarException se) {
			se.printStackTrace();
		}

		map.put("TotalFreeMem", mem.getFree() / 1024 / 1024);
		map.put("TotalUsedMem", mem.getUsed() / 1024 / 1024);
		map.put("TotalSystemMem", mem.getTotal() / 1024 / 1024);
		/*
		 * System.out.println("Actual total free system memory: " +
		 * mem.getActualFree() / 1024 / 1024 + " MB");
		 * System.out.println("Actual total used system memory: " +
		 * mem.getActualUsed() / 1024 / 1024 + " MB");
		 * System.out.println("Total free system memory ......: " +
		 * mem.getFree() / 1024 / 1024 + " MB");
		 * System.out.println("System Random Access Memory....: " + mem.getRam()
		 * + " MB"); System.out.println("Total system memory............: " +
		 * mem.getTotal() / 1024 / 1024 + " MB");
		 * System.out.println("Total used system memory.......: " +
		 * mem.getUsed() / 1024 / 1024 + " MB");
		 * System.out.println("\n**************************************\n");
		 */

		return map;
	}

	public HashMap<String, Object> getCpuInfo() throws SigarException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		CpuInfo cpuInfo;

		for (int i = 0; i < sigar.getCpuInfoList().length; i++) {
			CpuInfo[] info = sigar.getCpuInfoList();
			// System.out.println(info[i]);
		}
		cpuInfo = (sigar.getCpuInfoList())[0];
		map.put("model", cpuInfo.getModel());
		map.put("mhz", cpuInfo.getMhz());
		map.put("cores", cpuInfo.getTotalCores());
		map.put("vendor", cpuInfo.getVendor());

		return map;
	}

	/* Get a list of all filesystem roots on this system */
	public List<HashMap<String, Object>> getSystemSpace() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		File[] roots = File.listRoots();

		for (File root : roots) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("absolutePath", root.getAbsolutePath());
			map.put("totalSpace", root.getTotalSpace() / 1024 / 1024);
			map.put("freeSpace", root.getFreeSpace() / 1024 / 1024);
			map.put("usableSpace", root.getUsableSpace() / 1024 / 1024);
			list.add(map);
		}
		return list;
	}

	/*
	 * public List<HashMap<String, Object>> getNetworkInfo() throws
	 * SocketException { List<HashMap<String, Object>> list = new
	 * ArrayList<HashMap<String, Object>>(); Enumeration<NetworkInterface> nics
	 * = NetworkInterface.getNetworkInterfaces();
	 * 
	 * while( nics.hasMoreElements() ){ NetworkInterface nic =
	 * nics.nextElement(); if( !nic.isUp() ) continue;
	 * 
	 * System.out.println("Name : " + nic.getName());
	 * System.out.println("display name : " + nic.getDisplayName());
	 * System.out.println("MTU : " + nic.getMTU());
	 * System.out.print("H/W addr : "); if( nic.getHardwareAddress() != null ) {
	 * for( int i = 0; i < nic.getHardwareAddress().length; i++ ) {
	 * System.out.format("%02x", nic.getHardwareAddress()[i]); if( i <
	 * nic.getHardwareAddress().length - 1) System.out.print(":"); }
	 * System.out.println(); } System.out.println("inet addr : ");
	 * Enumeration<InetAddress> ipaddr = nic.getInetAddresses(); while(
	 * ipaddr.hasMoreElements() ) { System.out.println("\t" +
	 * ipaddr.nextElement().toString() ); } System.out.println("iface addr : ");
	 * List<InterfaceAddress> ifaddrs = nic.getInterfaceAddresses();
	 * for(InterfaceAddress ifaddr : ifaddrs ) { System.out.println("\t" +
	 * ifaddr.toString()); } System.out.println(); }
	 * 
	 * return list; }
	 */

	public TabularData getCpuInfo2() throws SigarException {

		TabularData tData = null;
		CpuInfo cpuInfo;
		HashMap<String, Object> map = new HashMap<String, Object>();

		Sigar sigar = new Sigar();

		for (int i = 0; i < sigar.getCpuInfoList().length; i++) {
			CpuInfo[] info = sigar.getCpuInfoList();
			// System.out.println(info[i]);
		}
		cpuInfo = (sigar.getCpuInfoList())[0];
		map.put("model", cpuInfo.getModel());
		map.put("mhz", cpuInfo.getMhz());
		map.put("cores", cpuInfo.getTotalCores());
		map.put("vendor", cpuInfo.getVendor());

		tData.put((CompositeData) map);

		return tData;
	}

	public HashMap<String, Object> getCpuPercUsage(/*CpuPerc cpu*/) throws SigarException {
		CpuPerc cpu = sigar.getCpuPerc();
		System.out.println(CpuPerc.format(cpu.getUser()) + "\t"
				+ CpuPerc.format(cpu.getSys()) + "\t"
				+ CpuPerc.format(cpu.getWait()) + "\t"
				+ CpuPerc.format(cpu.getNice()) + "\t"
				+ CpuPerc.format(cpu.getIdle()) + "\t"
				+ CpuPerc.format(cpu.getCombined()));
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("user",		CpuPerc.format(cpu.getUser()));
		map.put("sys",		CpuPerc.format(cpu.getSys()));
		map.put("wait",		CpuPerc.format(cpu.getWait()));
		map.put("nice",		CpuPerc.format(cpu.getNice()));
		map.put("idle",		CpuPerc.format(cpu.getIdle()));
		map.put("total",	CpuPerc.format(cpu.getCombined()));
		
		return map;
	}
	
	public static Double[] getMetric() throws SigarException {
        CpuPerc cpu = sigar.getCpuPerc();
        double system = cpu.getSys();
        double user = cpu.getUser();
        double idle = cpu.getIdle();
        System.out.println("idle: " +CpuPerc.format(idle) +", system: "+CpuPerc.format(system)+ ", user: "+CpuPerc.format(user));
        return new Double[] {system, user, idle};
    }

	public static void main(String[] args) throws Exception {
		Sigar sigar = new Sigar();
		SystemStatusImpl status = new SystemStatusImpl(sigar);
		// List<HashMap<String, Object>> list = new ArrayList<HashMap<String,
		// Object>>();
		// list = status.getMemoryInfo();
		// Set <String> keys = list.get(0).keySet();
		// for(String key : list.get(0).keySet()){
		// System.out.println("---> "+list.get(0).get(key));
		// }
		status.getJvmFreeMemory();
		status.getJvmMaxMemory();
		status.getJvmTotalMemory();

		// get memory info
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = status.getJvmMemoryInfoMB();

		Iterator<String> memKeys = map.keySet().iterator();
		while (memKeys.hasNext()) {
			String key = memKeys.next();
			System.out.println("key = " + key);
			System.out.println("value : " + map.get(key));
		}

		// get system space
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> spaces = new HashMap<String, Object>();
		list = status.getSystemSpace();
		
		for (int i = 0; i < list.size(); i++) {
			spaces = list.get(i);

			Iterator<String> spaceKeys = spaces.keySet().iterator();
			while (spaceKeys.hasNext()) {
				String key = spaceKeys.next();
				System.out.println("key = " + key);
				System.out.println("value : " + spaces.get(key));
			}
		}

		// get system memory
		HashMap<String, Object> sysMemMap = new HashMap<String, Object>();
		sysMemMap = status.getSystemMemoryInfo();

		Iterator<String> sysMemIt = sysMemMap.keySet().iterator();
		while (sysMemIt.hasNext()) {
			String key = sysMemIt.next();
			System.out.println("key = " + key);
			System.out.println("value : " + sysMemMap.get(key));
		}

		// get cpu info
		HashMap<String, Object> sysCpuMap = new HashMap<String, Object>();
		sysCpuMap = status.getCpuInfo();

		Iterator<String> sysCpuIt = sysCpuMap.keySet().iterator();
		while (sysCpuIt.hasNext()) {
			String key = sysCpuIt.next();
			System.out.println("key = " + key);
			System.out.println("value : " + sysCpuMap.get(key));
		}
		
		status.getCpuPercUsage();
		getMetric();
		
		sigar.close();

	}

}
