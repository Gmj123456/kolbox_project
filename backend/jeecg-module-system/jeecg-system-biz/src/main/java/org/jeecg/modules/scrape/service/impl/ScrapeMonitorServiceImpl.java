package org.jeecg.modules.scrape.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.scrape.entity.ScrapeMonitor;
import org.jeecg.modules.scrape.mapper.ScrapeMonitorMapper;
import org.jeecg.modules.scrape.service.IScrapeMonitorService;
import org.springframework.stereotype.Service;

@Service
public class ScrapeMonitorServiceImpl extends ServiceImpl<ScrapeMonitorMapper, ScrapeMonitor> implements IScrapeMonitorService {
}
