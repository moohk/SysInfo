package com.coe.lce.system;

import java.util.HashMap;
import java.util.List;

import javax.management.openmbean.TabularData;

import org.hyperic.sigar.SigarException;

public interface SystemStatus {
	int getProcessors();
	Long getJvmFreeMemory();
	Long getJvmMaxMemory();
	Long getJvmTotalMemory();
	HashMap<String, Object> getJvmMemoryInfoMB();
	HashMap<String, Object> getSystemMemoryInfo();
	HashMap<String, Object> getCpuInfo() throws SigarException;
	List<HashMap<String, Object>> getSystemSpace();
	TabularData getCpuInfo2() throws SigarException;
}
