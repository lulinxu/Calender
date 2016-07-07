package cn.ltwc.cft.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ltwc.cft.R;
import cn.ltwc.cft.adapter.HistoryOnTodayImgAdapter;
import cn.ltwc.cft.beans.HistoryOnTodayBeanJUHE;
import cn.ltwc.cft.beans.HistoryOnTodayImgBean;
import cn.ltwc.cft.http.HttpFactory;
import cn.ltwc.cft.http.ServiceResponce;
import cn.ltwc.cft.myinterface.ImgLoadListener;
import cn.ltwc.cft.view.MyListView;
import cn.ltwc.cft.view.TitleView;

public class HistoryDetailJUHEActivity extends BaseActivity implements ServiceResponce, ImgLoadListener {
	private TitleView title;
	private TextView year, titleName, content;
	private HistoryOnTodayBeanJUHE bean;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private MyListView imgList;
	private List<HistoryOnTodayImgBean> imgUrl;
	private HistoryOnTodayImgAdapter adpter;
	public static HistoryDetailJUHEActivity instance;
	public List<String> count;
	private String t = "";// 标题
	private String c = "";// 内容

	public HistoryDetailJUHEActivity() {
		super(R.layout.activity_juhe_history_detail);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		instance = this;
		title = (TitleView) findViewById(R.id.title);
		title.setTitletext("历史上的今天");
		year = (TextView) findViewById(R.id.juhe_history_year);
		titleName = (TextView) findViewById(R.id.juhe_history_title);
		content = (TextView) findViewById(R.id.juhe_history_event);
		imgList = (MyListView) findViewById(R.id.juhe_img_list);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		count = new ArrayList<String>();
		bean = (HistoryOnTodayBeanJUHE) getIntent().getSerializableExtra("bean");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
		imgUrl = new ArrayList<HistoryOnTodayImgBean>();
		adpter = new HistoryOnTodayImgAdapter(this, imgUrl, this);
		showWaitingDialog(this);
		HttpFactory.HistoryJUHEDetail(this, bean.getE_id());
	}

	@Override
	public void bindView() {
		// TODO Auto-generated method stub
		imgList.setAdapter(adpter);
		year.setText(bean.getYear());
		titleName.setText(bean.getTitle());
		// content.setText(bean.getEvent());
	}

	@Override
	public void httpSuccess(String result, int responseFlag) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(result);
			JSONArray array = object.optJSONArray("result");

			imgUrl.clear();
			count.clear();
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.optJSONObject(i);
					t = obj.optString("title");
					c = obj.optString("content");
					JSONArray a = obj.optJSONArray("picUrl");
					for (int j = 0; j < a.length(); j++) {
						JSONObject o = a.optJSONObject(j);
						String pic_title = o.optString("pic_title");
						String url = o.optString("url");
						HistoryOnTodayImgBean bean = new HistoryOnTodayImgBean(pic_title, url);

						imgUrl.add(bean);

					}
				}
				if (imgUrl.size() == 0) {
					hideWaitingDialog();
					titleName.setText(t);
					content.setText(c);
				}
				adpter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void httpTimeOut(int responseFlag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void httpError(int responseFlag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ImgLoad() {
		Log.d("AA", "count.size()=" + count.size());
		Log.d("AA", "imgUrl.size()=" + imgUrl.size());
		// TODO Auto-generated method stub
		if (count.size() >= imgUrl.size()) {
			hideWaitingDialog();
			titleName.setText(t);
			content.setText(c);
			imgList.setVisibility(View.VISIBLE);
		} else {
			imgList.setVisibility(View.GONE);
			showWaitingDialog(this);
		}
	}

}