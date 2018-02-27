package com.luxiliu.android.sample.scrollloadrecyclerview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luxiliu.android.widget.ScrollLoadRecyclerView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final SampleAdapter sampleAdapter = new SampleAdapter();
        sampleAdapter.addData(50);
        final ScrollLoadRecyclerView scrollLoadRecyclerView = findViewById(R.id.scroll_load_recycler_view);
        scrollLoadRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        scrollLoadRecyclerView.setAdapter(sampleAdapter);
        scrollLoadRecyclerView.setOnLoadListener(new ScrollLoadRecyclerView.OnLoadListener() {
            @Override
            public void onLoad() {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        sampleAdapter.addData(50);

                        scrollLoadRecyclerView.setLoading(false);

                        scrollLoadRecyclerView.setLoadingEnabled(false);
                    }
                }.execute();
            }
        });
    }

    private class SampleAdapter extends ScrollLoadRecyclerView.Adapter<SampleViewHolder> {
        int dataSie = 0;

        void addData(int dataSize) {
            this.dataSie += dataSize;
            notifyDataSetChanged();
        }

        @Override
        public SampleViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.view_item, parent, false);
            return new SampleViewHolder(itemView);
        }

        @Override
        public void onBindDataViewHolder(SampleViewHolder holder, int position) {
            holder.textView.setText(String.format(Locale.getDefault(), "Item: %d", position + 1));
        }

        @Override
        public int getDataItemCount() {
            return dataSie;
        }

        @Override
        public int getDataItemViewType() {
            return 0;
        }
    }

    private class SampleViewHolder extends ScrollLoadRecyclerView.ViewHolder {
        TextView textView;

        SampleViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
