package com.navi.dao;

	import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

	import org.json.JSONArray;
import org.json.JSONObject;

	import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

	public class RssNews {

		public JSONArray parseRss() {

			String rss = "http://rss.sina.com.cn/ent/hot_roll.xml";
			JSONArray news = new JSONArray();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				URL url = new URL(rss);
				
				XmlReader reader = new XmlReader(url);

				System.out.println("Rss获取新闻" + reader.getEncoding());
				SyndFeedInput input = new SyndFeedInput();
				
				SyndFeed feed = input.build(reader);

				
				List entries = feed.getEntries();

				
				for (int i = 0; i < entries.size(); i++) {
					SyndEntry entry = (SyndEntry) entries.get(i);
					JSONObject snews = new JSONObject();

					SyndContent description = entry.getDescription();

					
					snews.put("link", entry.getLink());
					snews.put("title", entry.getTitle().toString());
					snews.put("time", df.format(entry.getPublishedDate()));
					snews.put("description", description.getValue().toString());
					news.put(snews);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return news;
		}
	}

