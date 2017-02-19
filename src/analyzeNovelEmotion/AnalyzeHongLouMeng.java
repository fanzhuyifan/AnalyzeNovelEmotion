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

public class AnalyzeHongLouMeng extends ApplicationFrame
{
	//the number of windows and the size of windows (in words) when analyzing the emotion of HongLouMeng
	static int numOfWindows = 5000;
	static int windowSize = 20000;
	static String applicationTitle = "红楼梦中词语情感";
	static String chartTitle = "词语感情走势图";
	static String novelPath = "/Users/themacpro/Desktop/emotionOfNovel/小说/红楼梦.txt";
	static String dictPath = "/Users/themacpro/Desktop/emotionOfNovel/情感词汇本体27476词/dict.txt";
	private Novel hongLouMeng;
	//positions of the 120 chapters
	private ArrayList<Integer> indices;
	private double[] emotions;
	
	public AnalyzeHongLouMeng() throws IOException{
		super(applicationTitle);
		this.hongLouMeng = new Novel(novelPath,dictPath);
		this.emotions = this.hongLouMeng.getEmotion(AnalyzeHongLouMeng.windowSize, AnalyzeHongLouMeng.numOfWindows);
		this.calculateIndices();
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
	
	//calculate the position of the individual chapters
	private void calculateIndices(){
		this.indices = new ArrayList<Integer>();
		ArrayList<String> parsedText = this.hongLouMeng.getParsedText();
		for(int i = 0;i<parsedText.size();i++){
			if(parsedText.get(i).equals("手机")){
				indices.add(i);
			}
		}
	}
	
	private XYDataset createDataset(){
		XYSeriesCollection dataset = new XYSeriesCollection( );
		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		for(int i = 0;i<120;i++){
			series.add(new XYSeries(i));
		}
		//step between windows
		double d = (hongLouMeng.getWordCount()+0.0-windowSize)/numOfWindows;
		//the chapter
		int chapter = 0;
		for(int i = 0;i<emotions.length;i++){
			if (d*i>indices.get(chapter)-windowSize/2&&chapter<119){
				chapter++;
			}
			series.get(chapter).add((i+0.0)/numOfWindows,emotions[i]);
		}
		for(chapter = 0;chapter<120;chapter++){
			dataset.addSeries(series.get(chapter));
		}
		return dataset;
	}
	public static void main( String[ ] args ) throws IOException {
		AnalyzeHongLouMeng chart = new AnalyzeHongLouMeng();
		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
		System.out.printf("总词数:%d\n字典可识别词数:%d\n", 
				chart.hongLouMeng.getWordCount(),chart.hongLouMeng.getEmotionWordCount());
	}
}