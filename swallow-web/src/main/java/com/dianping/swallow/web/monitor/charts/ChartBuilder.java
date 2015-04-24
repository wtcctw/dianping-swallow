package com.dianping.swallow.web.monitor.charts;


import java.util.Arrays;
import java.util.Collection;

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


	
	public static HighChartsWrapper getHighChart(String title, String subTitle, StatsData... data) {
		
		return getHighChart(title, subTitle, Arrays.asList(data));
	}
	
	
	public static HighChartsWrapper getHighChart(String title, String subTitle, Collection<StatsData> data) {

		HighChartsWrapper hcw = new HighChartsWrapper();
		hcw.setTitle(title);
		hcw.setSubTitle(subTitle);

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
}