package analyzeNovelEmotion;

import org.jfree.chart.ChartPanel;

import java.io.IOException;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Analyze extends ApplicationFrame
{
	//the number of windows and the size of windows (in words) when analyzing the emotion of novel
	static int numOfWindows = 2000;
	static int windowSize = 4000;
	static String applicationTitle = "活着词语情感";
	static String chartTitle = "词语感情走势图";
	static String novelPath = "/Users/themacpro/Desktop/emotionOfNovel/小说/活着.txt";
	static String dictPath = "/Users/themacpro/Desktop/emotionOfNovel/情感词汇本体27476词/dict.txt";
	private Novel novel;
	private double[] emotions;
	
	public Analyze() throws IOException{
		super(applicationTitle);
		this.novel = new Novel(novelPath,dictPath);
		this.emotions = this.novel.getEmotion(Analyze.windowSize, Analyze.numOfWindows);
		JFreeChart lineChart = ChartFactory.createXYLineChart(
				chartTitle,
				"小说进度","平均情感",
				createDataset(),
				PlotOrientation.VERTICAL,
				true,true,false);
		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		setContentPane( chartPanel );
	}
	
	private XYDataset createDataset(){
		XYSeriesCollection dataset = new XYSeriesCollection( );
		XYSeries series = new XYSeries(0);
		//step between windows
		double d = (novel.getWordCount()+0.0-windowSize)/numOfWindows;
		for(int i = 0;i<emotions.length;i++){
			series.add((i+0.0)/numOfWindows,emotions[i]);
		}
		dataset.addSeries(series);
		return dataset;
	}
	public static void main( String[ ] args ) throws IOException {
		Analyze chart = new Analyze();
		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
		System.out.printf("总词数:%d\n字典可识别词数:%d\n", 
				chart.novel.getWordCount(),chart.novel.getEmotionWordCount());
	}
}