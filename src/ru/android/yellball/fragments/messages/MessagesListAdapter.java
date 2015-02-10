package ru.android.yellball.fragments.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.android.yellball.R;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.managers.AudioMessageManager;
import ru.android.yellball.utils.FormatterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 31.01.2015.
 */
public class MessagesListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;

    private List<AudioMessage> messages = new ArrayList<>();
    private List<View> inflatedViews = new ArrayList<>();

    public MessagesListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.messages = AudioMessageManager.getMessages();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflatedViews.size() <= position) {
            inflatedViews.addAll(Collections.<View>nCopies(position - inflatedViews.size() + 1, null));
        }

        if (inflatedViews.get(position) == null) {
            View view = layoutInflater.inflate(R.layout.message_item, parent, false);
            inflatedViews.set(position, view);
        }

        View view = inflatedViews.get(position);
        bindView(view, context, messages.get(position));
        return view;
    }

    public void bindView(View view, Context context, AudioMessage message) {
        ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView authorView = (TextView) view.findViewById(R.id.author);
        TextView durationView = (TextView) view.findViewById(R.id.duration);
        TextView createdView = (TextView) view.findViewById(R.id.created);

        //avatarView.setBackgroundColor(0xffff0000); // TODO
        titleView.setText(message.getTitle());
        authorView.setText(message.getAuthor());
        durationView.setText(FormatterUtil.formatDuration(message.getDuration()));
        createdView.setText(FormatterUtil.formatDate(message.getCreated()));
    }
}
