package id.ac.uns.posbindu.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import id.ac.uns.posbindu.databinding.MenuCardBinding

class MenuAdapter(
    private var menu: List<Menu>, private val onClickListener: (menu: Menu) -> Unit,
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    fun updateMenu(menu: List<Menu>) {
        this.menu = menu
        notifyDataSetChanged()
    }

    class ViewHolder(
        val binding: MenuCardBinding, val onClickListener: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClickListener(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MenuCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { position -> onClickListener(menu[position]) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menu[position]
        holder.binding.apply {
            illustration.load(menu.image, root.context.imageLoader)
            textViewTitle.text = menu.title
        }
    }

    override fun getItemCount() = menu.size
}