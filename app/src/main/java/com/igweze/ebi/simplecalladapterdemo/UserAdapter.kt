package com.igweze.ebi.simplecalladapterdemo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class UserAdapter: RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var users: List<Result> = listOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_user, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    fun setUsers(users: List<Result>) {
        this.users = users
        notifyDataSetChanged()
    }

    private lateinit var context: Context;

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val tvUserName: TextView = view.findViewById(R.id.tvUserName)

        fun bind(result: Result) {
            tvUserName.text = result.user.name.let { "${it.title}. ${it.first} ${it.last}" }
        }
    }
}