package id.ac.uns.posbindu.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.uns.posbindu.databinding.MultipleChoiceQuestionChoiceCardBinding

class MultipleChoiceQuestionChoiceAdapter :
    RecyclerView.Adapter<MultipleChoiceQuestionChoiceAdapter.ViewHolder>() {
    private var recyclerView: RecyclerView? = null

    var choices = emptyList<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selection: Int? = null
        set(value) {
            if (value != field) {
                field = value
                field?.let { notifyItemChanged(it - 1) }
            }
        }

    class ViewHolder(
        val binding: MultipleChoiceQuestionChoiceCardBinding,
        val onClickListener: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClickListener(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MultipleChoiceQuestionChoiceCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) { position ->
        for (i in choices.indices) (recyclerView?.findViewHolderForAdapterPosition(i) as? ViewHolder)
            ?.binding?.root?.isChecked = i == position
        selection = position + 1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            root.isChecked = position == selection?.minus(1)
            textViewText.text = choices[position]
        }
    }

    override fun getItemCount() = choices.size
}