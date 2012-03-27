package com.dianping.cat.status;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

import com.dianping.cat.message.spi.MessageStatistics;
import com.dianping.cat.status.model.entity.DiskSpaceInfo;
import com.dianping.cat.status.model.entity.GcInfo;
import com.dianping.cat.status.model.entity.MemoryInfo;
import com.dianping.cat.status.model.entity.MessageInfo;
import com.dianping.cat.status.model.entity.OsInfo;
import com.dianping.cat.status.model.entity.RuntimeInfo;
import com.dianping.cat.status.model.entity.StatusInfo;
import com.dianping.cat.status.model.entity.ThreadInfo;
import com.dianping.cat.status.model.transform.BaseVisitor;

class StatusInfoCollector extends BaseVisitor {
	private MessageStatistics m_statistics;

	public StatusInfoCollector(MessageStatistics statistics) {
		m_statistics = statistics;
	}

	long getGcCount(List<GarbageCollectorMXBean> mxbeans) {
		long count = 0;

		for (GarbageCollectorMXBean mxbean : mxbeans) {
			if (mxbean.isValid()) {
				count += mxbean.getCollectionCount();
			}
		}

		return count;
	}

	long getGcTime(List<GarbageCollectorMXBean> mxbeans) {
		long time = 0;

		for (GarbageCollectorMXBean mxbean : mxbeans) {
			if (mxbean.isValid()) {
				time += mxbean.getCollectionTime();
			}
		}

		return time;
	}

	boolean isInstanceOfInterface(Class<?> clazz, String interfaceName) {
		if (clazz == Object.class) {
			return false;
		} else if (clazz.getName().equals(interfaceName)) {
			return true;
		}

		Class<?>[] interfaceclasses = clazz.getInterfaces();

		for (Class<?> interfaceClass : interfaceclasses) {
			if (isInstanceOfInterface(interfaceClass, interfaceName)) {
				return true;
			}
		}

		return isInstanceOfInterface(clazz.getSuperclass(), interfaceName);
	}

	@Override
	public void visitDiskSpace(DiskSpaceInfo diskSpace) {
		File workingDir = new File(".");

		diskSpace.setTotal(workingDir.getTotalSpace());
		diskSpace.setFree(workingDir.getFreeSpace());
		diskSpace.setUsable(workingDir.getUsableSpace());
	}

	@Override
	public void visitGc(GcInfo gc) {
		List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();

		gc.setCount(getGcCount(beans));
		gc.setTime(getGcTime(beans));
	}

	@Override
	public void visitMemory(MemoryInfo memory) {
		MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
		Runtime runtime = Runtime.getRuntime();

		memory.setTotal(runtime.totalMemory());
		memory.setFree(runtime.freeMemory());
		memory.setHeapUsage(bean.getHeapMemoryUsage().getUsed());
		memory.setNonHeapUsage(bean.getNonHeapMemoryUsage().getUsed());

		memory.setGc(new GcInfo());
		super.visitMemory(memory);
	}

	@Override
	public void visitMessage(MessageInfo message) {
		if (m_statistics != null) {
			message.setProduced(m_statistics.getProduced());
			message.setOverflowed(m_statistics.getOverflowed());
			message.setBytes(m_statistics.getBytes());
		}
	}

	@Override
	public void visitOs(OsInfo os) {
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

		os.setArch(bean.getArch());
		os.setName(bean.getName());
		os.setVersion(bean.getName());
		os.setAvailableProcessors(bean.getAvailableProcessors());
		os.setSystemLoadAverage(bean.getSystemLoadAverage());

		// for Sun JDK
		if (isInstanceOfInterface(bean.getClass(), "com.sun.management.OperatingSystemMXBean")) {
			com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) bean;

			os.setTotalPhysicalMemory(b.getTotalPhysicalMemorySize());
			os.setFreePhysicalMemory(b.getFreePhysicalMemorySize());
			os.setTotalSwapSpace(b.getTotalSwapSpaceSize());
			os.setFreeSwapSpace(b.getFreeSwapSpaceSize());
			os.setProcessTime(b.getProcessCpuTime());
			os.setCommittedVirtualMemory(b.getCommittedVirtualMemorySize());
		}
	}

	@Override
	public void visitRuntime(RuntimeInfo runtime) {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();

		runtime.setStartTime(bean.getStartTime());
		runtime.setUpTime(bean.getUptime());
	}

	@Override
	public void visitStatus(StatusInfo status) {
		status.setOs(new OsInfo());
		status.setDiskSpace(new DiskSpaceInfo());
		status.setRuntime(new RuntimeInfo());
		status.setMemory(new MemoryInfo());
		status.setThread(new ThreadInfo());
		status.setMessage(new MessageInfo());

		super.visitStatus(status);
	}

	@Override
	public void visitThread(ThreadInfo thread) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();

		thread.setCount(bean.getThreadCount());
		thread.setDaemonCount(bean.getDaemonThreadCount());
		thread.setPeekCount(bean.getPeakThreadCount());
		thread.setTotalStartedCount(bean.getTotalStartedThreadCount());
	}
}