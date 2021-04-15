package id.ac.uns.posbindu.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.uns.posbindu.databinding.FormSectionCardBinding

class FormSectionAdapter(
    private var sections: List<Section>, private val onClickListener: (section: Section) -> Unit
) : RecyclerView.Adapter<FormSectionAdapter.ViewHolder>() {
    fun updateSections(sections: List<Section>) {
        this.sections = sections
        notifyDataSetChanged()
    }

    class ViewHolder(
        val binding: FormSectionCardBinding, val onClickListener: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClickListener(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        FormSectionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { position -> onClickListener(sections[position]) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.binding.apply {
            textViewTitle.text = section.title
            subtitle.text = section.subtitle
        }
    }

    override fun getItemCount() = sections.size
}