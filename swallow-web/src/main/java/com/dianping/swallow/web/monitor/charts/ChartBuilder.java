package com.dianping.swallow.web.monitor.charts;


import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper.PlotOption;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper.PlotOptionSeries;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper.Series;

/**
 * 构建展示数据
 * 
 * @author mengwenchao
 *
 *         2014年7月9日 上午10:32:51
 */
public class ChartBuilder {
	
	protected static final Logger logger     = LoggerFactory.getLogger(ChartBuilder.class);

	public static HighChartsWrapper getHighChart(String title, String subTitle, String yAxisTitle, StatsData... data) {
		
		return getHighChart(title, subTitle, yAxisTitle, Arrays.asList(data));
	}

	public static HighChartsWrapper getHighChart(String title, String subTitle, String yAxisTitle, Collection<StatsData> data) {
		
		return getHighChart(title, subTitle, "时间", yAxisTitle, data);
	}
	
	
	public static HighChartsWrapper getHighChart(String title, String subTitle,String xAxisTitle, String yAxisTitle, Collection<StatsData> data) {

		insertData(data);
		
		HighChartsWrapper hcw = new HighChartsWrapper();
		hcw.setTitle(title);
		hcw.setSubTitle(subTitle);
		
		hcw.setxAxisTitle(xAxisTitle);
		hcw.setyAxisTitle(yAxisTitle);

		Series[] series = new Series[data.size()];
		int i = 0;

		long realInterval = 0;
		long startTime = 0;
		for (StatsData ss : data) {
			startTime = ss.getStart();
			realInterval = ss.getIntervalTimeSeconds()*1000;
			Series se = new Series();
			se.setData(ss.getArrayData());
			se.setName(ss.getInfo().getDesc());
			series[i++] = se;
		}
		hcw.setSeries(series);

		PlotOption plotOption = new PlotOption();
		PlotOptionSeries pos = new PlotOptionSeries();

		pos.setPointStart(startTime);
		pos.setPointInterval(realInterval);
		plotOption.setSeries(pos);

		hcw.setPlotOption(plotOption);
		return hcw;
	}

	//数据插值，防止各个曲线数据不等
	private static void insertData(Collection<StatsData> data) {
		
		long startMin = Long.MAX_VALUE;
		for(StatsData ss : data){
			if(ss.getStart() < startMin){
				startMin = ss.getStart();
				if(logger.isInfoEnabled()){
					logger.info("[insertData][min]" + ss);
				}
			}
		}
		for(StatsData ss : data){
			ss.minToTime(startMin);
		}
	}
}
