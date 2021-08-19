package ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.R;

public class JournalViewHolder extends RecyclerView.ViewHolder {

    private TextView title, description, timeStamp, username;
    private ImageView post, share;

    private Context context;

    public JournalViewHolder(@NonNull View view, Context context) {
        super(view);
        this.context = context;
        title = view.findViewById(R.id.journalTitle);
        description = view.findViewById(R.id.journalDesc);
        timeStamp = view.findViewById(R.id.journalTimeStamp);
        post = view.findViewById(R.id.journalImage);
        username = view.findViewById(R.id.journalUsername);
        share = view.findViewById(R.id.journalShare);
    }

    public ImageView getShare() {
        return share;
    }

    public void setShare(ImageView share) {
        this.share = share;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public TextView getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TextView timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ImageView getPost() {
        return post;
    }

    public void setPost(ImageView post) {
        this.post = post;
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }
}
