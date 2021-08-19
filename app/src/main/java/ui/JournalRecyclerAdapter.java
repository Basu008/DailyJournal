package ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalViewHolder>{

    private List<Journal> journalList;
    private Context context;

    public JournalRecyclerAdapter(List<Journal> journalList, Context context) {
        this.journalList = journalList;
        this.context = context;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_view_holder, parent, false);
        return new JournalViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        String imageUrl;

        holder.getTitle().setText(journal.getTitle());
        holder.getDescription().setText(journal.getThought());
        imageUrl = journal.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);

        holder.getTimeStamp().setText(timeAgo);

        holder.getUsername().setText(journal.getUsername());

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(holder.getPost());

        holder.getShare().setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String url = "https://github.com/Basu008/DailyJournal";
            intent.putExtra(Intent.EXTRA_TEXT, "Hey there!\nI just posted a new update about my day" +
                    " on DailyJournal app\nCheck it out :" + url);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }
}
