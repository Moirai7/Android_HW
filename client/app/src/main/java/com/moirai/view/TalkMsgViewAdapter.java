
package com.moirai.view;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TalkMsgViewAdapter extends BaseAdapter {
	private TextView contant;

	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
  //  private static final String TAG = TalkMsgViewAdapter.class.getSimpleName();

    private List<Info> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    public TalkMsgViewAdapter(Context context, List<Info> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	Info entity = coll.get(position);
	 	if (entity.getSendUser().contains(Constant.USERNAME))
	 	{
            System.out.printf("send::"+entity.getSendUser());
            return IMsgViewType.IMVT_TO_MSG;
	 	}else{
            System.out.printf("rev::"+entity.getReceiver());
            return IMsgViewType.IMVT_COM_MSG;
	 	}

	}


	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {

    	Info entity = coll.get(position);
    	boolean isComMsg;
        if (!entity.getSendUser().equals(Constant.USERNAME))
        {
            isComMsg = true;
        }else{
            isComMsg = false;
        }
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg)
			  {
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }

	    viewHolder.tvSendTime.setText(entity.getTime());
	    viewHolder.tvUserName.setText(entity.getSendUser());
	    viewHolder.tvContent.setText(entity.getDetail());

        contant = (TextView)convertView.findViewById(R.id.tv_chatcontent);
        contant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ctx,GestureActivity.class);
                ctx.startActivity(intent);
            }
        });
	    
	    return convertView;
    }

    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }
}
