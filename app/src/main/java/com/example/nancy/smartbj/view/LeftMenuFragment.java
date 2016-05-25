package com.example.nancy.smartbj.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.domain.NewsCenterData;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧菜单的fragment
 * Created by Nancy on 2016/5/23.
 */
public class LeftMenuFragment extends BaseFragment {
    private List<NewsCenterData.NewsData> menuDataList = new ArrayList<>();
    private ListView lv_leftMenu;
    private MyAdapter adapter;
    private int selectPosition;//点击的菜单条目位置
    private OnSwitchPageListener switchPageListener;

    @Override
    public View initView() {

        //显示左侧菜单的listView
        lv_leftMenu = new ListView(mainActivity);

        //背景是黑色
        lv_leftMenu.setBackgroundColor(Color.BLACK);
        //选中拖动的背景颜色 设置成透明色 todo 不设置似乎在低版本上会拖动的时候样子像全选
        lv_leftMenu.setCacheColorHint(Color.TRANSPARENT);
        //设置选中时会透明背景 颜色的变化用selector实现
        lv_leftMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //不要分割线（高度设成0就没有了）
        lv_leftMenu.setDividerHeight(0);

        //距离顶部距离45px
        lv_leftMenu.setPadding(0, 45, 0, 0);

        return lv_leftMenu;
    }

    /**
     * 由外部调用，来设置侧滑菜单内容的数据
     */
    public void setLeftMenuData(List<NewsCenterData.NewsData> newsDataList) {
        this.menuDataList = newsDataList;

        adapter.notifyDataSetChanged();//设置好数据后通知刷新数据
    }

    @Override
    public void initEvent() {
        lv_leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //保存选中的位置
                selectPosition = position;
                //更新界面（让getView再判断一次是否设置enable-->通过selector改变颜色）
                adapter.notifyDataSetChanged();

                //控制新闻中心，四个新闻页面的显示
//                mainActivity.getMainContentFragment().leftMenuClickSwitchPage(selectPosition);
                //此处用两种方法实现切换页面:如果有接口回调就调用接口回调,没有的话就通过页面的层级关系调用
                if (switchPageListener != null) {
                    switchPageListener.switchPage(selectPosition);
                } else {
                    mainActivity.getMainContentFragment().leftMenuClickSwitchPage(selectPosition);
                }

                //切换SlidingMenu的开关
                mainActivity.getSlidingMenu().toggle();

            }
        });
    }

    @Override
    public void initData() {
        adapter = new MyAdapter();
        lv_leftMenu.setAdapter(adapter);

    }

    public interface OnSwitchPageListener {
        void switchPage(int selectionIndex);
    }

    public void setOnSwitchPageListener(OnSwitchPageListener listener) {
        switchPageListener = listener;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menuDataList.size();//为了防止空指针，可以在外部类成员变量初始化的时候就new出来
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.leftmenu_list_item, null);
            }
            //设置标题
            TextView tv_currentView = (TextView) convertView;
            tv_currentView.setText(menuDataList.get(position).title);
            //判断是否被选中
            tv_currentView.setEnabled(position == selectPosition);

            return convertView;
        }
    }
}
