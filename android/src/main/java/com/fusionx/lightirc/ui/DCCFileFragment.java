package com.fusionx.lightirc.ui;

import com.fusionx.bus.Subscribe;
import com.fusionx.bus.ThreadType;
import com.fusionx.lightirc.R;
import com.fusionx.lightirc.event.OnConversationChanged;
import com.fusionx.lightirc.misc.FragmentType;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.fusionx.relay.conversation.Conversation;
import co.fusionx.relay.internal.dcc.base.RelayDCCFileConnection;
import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;
import co.fusionx.relay.dcc.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.dcc.event.file.DCCFileProgressEvent;

import static com.fusionx.lightirc.util.MiscUtils.getBus;

public class DCCFileFragment extends BaseIRCFragment {

    private final EventHandler mEventHandler = new EventHandler();

    private ListView mListView;

    private Conversation mConversation;

    private DCCFileAdapter mAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final OnConversationChanged event = getBus().getStickyEvent(OnConversationChanged.class);
        mConversation = event.conversation;

        mAdapter = new DCCFileAdapter(getActivity(), getFileConversation().getFileConnections());
    }

    @Override
    public View onCreateView(final LayoutInflater inflate, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflate.inflate(R.layout.dcc_file_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mConversation.registerForEvents(mEventHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mConversation.unregisterFromEvents(mEventHandler);
    }

    @Override
    public FragmentType getType() {
        return FragmentType.DCCFILE;
    }

    @Override
    public boolean isValid() {
        return mConversation.isValid();
    }

    public RelayDCCFileConversation getFileConversation() {
        return (RelayDCCFileConversation) mConversation;
    }

    public class DCCFileAdapter extends BaseAdapter {

        private final LayoutInflater mLayoutInflater;

        private final List<RelayDCCFileConnection> mConnectionList;

        public DCCFileAdapter(final Context context, final Collection<RelayDCCFileConnection>
                dccConnectionList) {
            mLayoutInflater = LayoutInflater.from(context);
            mConnectionList = new ArrayList<>(dccConnectionList);
        }

        @Override
        public int getCount() {
            return mConnectionList.size();
        }

        @Override
        public RelayDCCFileConnection getItem(final int position) {
            return mConnectionList.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.dcc_file_list_item, parent, false);
            }
            final RelayDCCFileConnection connection = getItem(position);

            final TextView title = (TextView) convertView
                    .findViewById(R.id.dcc_file_list_item_name);
            title.setText(connection.getFileName());

            final TextView progress = (TextView) convertView
                    .findViewById(R.id.dcc_file_list_item_progress);
            progress.setText(String.format(getActivity().getString(R.string.dcc_progress_complete),
                    connection.getProgress()));

            return convertView;
        }

        public void replaceAll(final Collection<RelayDCCFileConnection> fileConnections) {
            mConnectionList.clear();
            mConnectionList.addAll(fileConnections);
            notifyDataSetChanged();
        }
    }

    private class EventHandler {

        @Subscribe(threadType = ThreadType.MAIN)
        public void onFileProgress(final DCCFileProgressEvent event) {
            mAdapter.notifyDataSetChanged();
        }

        @Subscribe(threadType = ThreadType.MAIN)
        public void onNewFileConnection(final DCCFileGetStartedEvent event) {
            mAdapter.replaceAll(event.conversation.getFileConnections());
        }
    }
}