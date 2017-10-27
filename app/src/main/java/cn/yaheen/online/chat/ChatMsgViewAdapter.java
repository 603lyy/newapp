package cn.yaheen.online.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.yaheen.online.R;


/**
 * 消息ListView的Adapter
 *
 * @author way
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
    }

    private static final int ITEMCOUNT = 2;// 消息类型的总数
    private List<ChatMsgEntity> coll;// 消息对象数组
    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
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

    /**
     * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
     */
    public int getItemViewType(int position) {
        ChatMsgEntity entity = coll.get(position);

        if (entity.getMsgType()) {//收到的消息
            return IMsgViewType.IMVT_COM_MSG;
        } else {//自己发送的消息
            return IMsgViewType.IMVT_TO_MSG;
        }
    }

    /**
     * Item类型的总数
     */
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMsgEntity entity = coll.get(position);
        boolean isComMsg = entity.getMsgType();
        Integer type = entity.getType();
        ViewHolder viewHolder = null;
//		if (convertView == null) {
        if (isComMsg) {   //是否为收到的信息
            if (type.equals(0)) {   //如果是字符类型
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_text_left, null);
            } else { //如果是图片类型
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_img_left, null);
            }

        } else {
            if (type.equals(0)) {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_text_right, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_img_right, null);
            }

        }

        viewHolder = new ViewHolder();
        viewHolder.tvSendTime = (TextView) convertView
                .findViewById(R.id.tv_sendtime);
        viewHolder.tvUserName = (TextView) convertView
                .findViewById(R.id.tv_username);

        if (type.equals(0)) {
            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent);
        } else {
            viewHolder.imgContent = (ImageView) convertView
                    .findViewById(R.id.tv_chatcontent);
        }


        viewHolder.isComMsg = isComMsg;

        convertView.setTag(viewHolder);


//		} else {
//
//
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
        try {
            viewHolder.tvSendTime.setText(entity.getDate());
            viewHolder.tvUserName.setText(entity.getName());
            //viewHolder.tvContent.setText(entity.getMessage());
            if (type.equals(0)) {
                viewHolder.tvContent.setText(entity.getMessage());
            } else {
                viewHolder.imgContent.setImageResource(R.drawable.ha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public ImageView imgContent;

        public boolean isComMsg = true;
    }

}
