package org.brightify.rxandroidutils

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.view.clicks
import rx.Observable
import rx.Observer
import rx.lang.kotlin.PublishSubject
import java.util.*

/**
 * @author <a href="mailto:hyblmatous@gmail.com">Matous Hybl</a>
 */

class RxSingleItemAdapter<MODEL : Any, BINDING : ViewDataBinding>(@LayoutRes private val layoutId: Int)
: RecyclerView.Adapter<RxItemViewHolder<BINDING>>(), Observer<ArrayList<MODEL>> {

    var data = ArrayList<MODEL>()

    private val itemClickSubject = PublishSubject<Pair<Int, MODEL>>()

    val itemClick: Observable<Pair<Int, MODEL>>
        get() = itemClickSubject.asObservable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxItemViewHolder<BINDING>? {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return RxItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RxItemViewHolder<BINDING>, position: Int) {
        binding(data[position], holder.binding)
        holder.itemView.clicks()
                .map { Pair(position, data[position]) }
                .subscribe(itemClickSubject)
    }

    override fun getItemCount(): Int = data.count()

    override fun onError(e: Throwable?) {
        if (e != null) {
            throw e
        }
    }

    override fun onNext(list: ArrayList<MODEL>?) {
        if (list != null) {
            data = list
            notifyDataSetChanged()
        }
    }

    override fun onCompleted() {

    }

    val removeItem: (position: Int) -> Unit = {
        data.removeAt(it)
        notifyItemRemoved(it)
    }

    private var binding: (MODEL, BINDING) -> Unit = { model, binding -> Unit }

    fun bind(recyclerView: RecyclerView, binding: (MODEL, BINDING) -> Unit) {
        this.binding = binding
        recyclerView.adapter = this
    }

}

class RxItemViewHolder<BINDING : ViewDataBinding>(view: View) : RecyclerView.ViewHolder(view) {

    val binding: BINDING

    init {
        binding = DataBindingUtil.bind(view)
    }
}

open class RxMultipleItemsAdapter : RecyclerView.Adapter<ItemViewHolder>(), Observer<ArrayList<Any>> {

    var data = ArrayList<Any>()

    private var dataItemBinding: (Int, List<Any>) -> Int = { position, data -> 0 }

    private var binding: (ViewDataBinding, Any) -> Unit = { binding, item -> Unit }

    private val itemClickSubject = PublishSubject<Pair<Int, Any>>()

    val itemClick: Observable<Pair<Int, Any>>
        get() = itemClickSubject.asObservable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding(holder.binding, data[position])
        holder.itemView.clicks()
                .map { Pair(position, data[position]) }
                .subscribe(itemClickSubject)
    }

    override fun getItemViewType(position: Int): Int {
        return dataItemBinding(position, data)
    }

    override fun onCompleted() {

    }

    override fun onNext(list: ArrayList<Any>?) {
        if (list != null) {
            data = list
            notifyDataSetChanged()
        }
    }

    override fun onError(e: Throwable?) {
        if (e != null) {
            throw e
        }
    }

    fun bind(recyclerView: RecyclerView, dataItemBinding: (position: Int, data: List<Any>) -> Int, binding: (ViewDataBinding, item: Any) -> Unit) {
        this.dataItemBinding = dataItemBinding
        this.binding = binding

        recyclerView.adapter = this
    }

    val removeItem: (position: Int) -> Unit = {
        data.removeAt(it)
        notifyItemRemoved(it)
    }

}

class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val binding: ViewDataBinding

    init {
        binding = DataBindingUtil.bind(view)
    }
}

class RxItemTouchHelperCallback(private val dragFlags: Int, private val swipeFlags: Int) : ItemTouchHelper.Callback() {

    val rxMove = PublishSubject<MovementData>()

    val rxSwipe = PublishSubject<Int>()

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int
            = makeMovementFlags(dragFlags, swipeFlags)

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        rxMove.onNext(MovementData(viewHolder?.adapterPosition!!, target?.adapterPosition!!))
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        rxSwipe.onNext(viewHolder?.adapterPosition!!)
    }

    data class MovementData(val initialPosition: Int, val endPosition: Int)
}
