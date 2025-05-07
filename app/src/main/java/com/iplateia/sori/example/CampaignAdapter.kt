package com.iplateia.sori.example

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iplateia.sorisdk.SORIAudioRecognizer
import com.iplateia.sorisdk.SORICampaign
import com.squareup.picasso.Picasso

/**
 * Adapter for displaying a list of campaigns in a RecyclerView.
 *
 * @param items The initial list of campaigns to display.
 */
class CampaignAdapter(private val items: MutableList<SORICampaign>) : RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.campaign_image)
        val title: TextView = view.findViewById(R.id.campaign_name)
        val desc: TextView = view.findViewById(R.id.campaign_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.campaign_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the data to the views in the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Load the remote image using Picasso
        Picasso.get()
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.image)
        holder.title.text = item.name
        holder.desc.text = item.description
        holder.itemView.setOnClickListener {
            if (!item.actionUrl.isNullOrEmpty()) {
                val context = it.context

                // Let the SORI SDK handle the action URL
                // Opens the actionUrl with default browser or handles it by proper schema/url handler
                // defined in the user's device.
                SORIAudioRecognizer.shared().handleActionURL(context, item.actionUrl!!)

                // Or just open the URL in a browser manually.
                // Please uncomment the following code if you want to open the URL in a browser manually.

                /*
                // Create an intent to open the URL in a browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.actionUrl))

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Check if there is an app that can handle the intent
                    val packageManager = context.packageManager

                    // Handle exceptions by notifying the user or ignoring
                    if (intent.resolveActivity(packageManager) == null) {
                        Toast.makeText(context, "No default browser found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error opening link", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
                */
            }
        }
    }

    override fun getItemCount() = items.size

    /**
     * Adds a new item to the beginning of the list and notifies the adapter.
     *
     * @param item The new campaign to add.
     */
    fun addItem(item: SORICampaign) {
        // When adding a new item, insert it at the beginning of the list
        items.add(0, item)
        notifyItemInserted(0)
    }

    /**
     * Removes all items
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }
}