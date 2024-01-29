

package com.dst.testapp

import androidx.fragment.app.Fragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.dst.testapp.atv.model.TreeNode
import com.dst.testapp.atv.view.AndroidTreeView
import com.dst.testapp.sd.HeaderListItem
import com.dst.testapp.sd.ListItemInterface
import com.dst.testapp.sd.ListItemRecursive
import com.dst.testapp.sd.TextListItem


@JvmSuppressWildcards(false)
abstract class TreeListFragment : Fragment(), TreeNode.TreeNodeClickListener {
    private var tView: AndroidTreeView? = null

    protected abstract val items: List<ListItemInterface>

    class ListItemHolder(context: Context) : TreeNode.BaseNodeViewHolder<Pair<ListItemInterface, Int>>(context) {
        private var mArrowView: ImageView? = null

        private fun adjustListView(view: View, li: ListItemInterface) {
            val mText1 = li.text1
            val mText2 = li.text2
            val text1Empty = mText1?.toString().isNullOrEmpty()
            val text2Empty = mText2?.toString().isNullOrEmpty()
            val text1 = view.findViewById<TextView>(android.R.id.text1)
            val text2 = view.findViewById<TextView>(android.R.id.text2)
            if (text1Empty)
                text1.visibility = View.GONE
            else {
                text1.text = mText1
                text1.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    text1.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                }
            }
            if (text2Empty)
                text2.visibility = View.GONE
            else {
                text2.text = mText2
                text2.visibility = View.VISIBLE
                text2.setTextIsSelectable(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    text2.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                }
            }
            if (text1Empty != text2Empty) {
                val remaining = if (text1Empty) text2 else text1
                val params = remaining.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.CENTER_VERTICAL or RelativeLayout.CENTER_IN_PARENT,
                        remaining.id)
            }
        }

        private fun getListView(li: ListItemInterface, inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean): View {
            val view = inflater.inflate(android.R.layout.simple_list_item_2, root, attachToRoot)
            adjustListView(view, li)
            return view
        }

        private fun decorateTextView(text: TextView, ctxt: Context,
                                     attr: Int, def: Int) {
            val textAppearenceRes: Int
            val ta = ctxt.obtainStyledAttributes(intArrayOf(attr))
            textAppearenceRes = ta.getResourceId(0, def)
            ta.recycle()
            TextViewCompat.setTextAppearance(text, textAppearenceRes)
        }

        private fun getTextListView(li: TextListItem, inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean): View {
            val view = inflater.inflate(android.R.layout.simple_list_item_2, root, attachToRoot)
            adjustListView(view, li)
            val text1 = view.findViewById<TextView>(android.R.id.text1)
            decorateTextView(text1, inflater.context, android.R.attr.textAppearanceMedium,
                    android.R.style.TextAppearance_Medium)
            return view
        }

        private fun getHeaderListL1View(li: HeaderListItem, inflater: LayoutInflater): View {
            val ctxt = inflater.context
            val rl = RelativeLayout(ctxt)
            val text = TextView(ctxt)
            decorateTextView(text, ctxt, android.R.attr.textAppearanceLarge,
                    android.R.style.TextAppearance_Large)
            text.text = li.text1
            text.gravity = Gravity.CENTER_HORIZONTAL
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            text.layoutParams = lp
            rl.addView(text)
            return rl
        }

        private fun getHeaderListL2View(li: HeaderListItem, inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean): View {
            val view = inflater.inflate(R.layout.list_header, root, attachToRoot)

            view.findViewById<TextView>(android.R.id.text1).text = li.text1
            return view
        }

        private fun getHeaderListView(li: HeaderListItem, inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean): View {
            val level = li.headingLevel
            return if (level == 1) getHeaderListL1View(li, inflater) else getHeaderListL2View(li, inflater, root, attachToRoot)
        }

        private fun getRecursiveListView(li: ListItemRecursive, inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean): View {
            val view = inflater.inflate(R.layout.list_recursive, root, attachToRoot)
            adjustListView(view, li)
            return view
        }

        override fun createNodeView(node: TreeNode, itemPair: Pair<ListItemInterface, Int>): View {
            val item = itemPair.first
            val level = itemPair.second
            val view: View = when (item) {
                is ListItemRecursive -> getRecursiveListView(item, LayoutInflater.from(context), null, false)
                is HeaderListItem -> getHeaderListView(item, LayoutInflater.from(context), null, false)
                is TextListItem -> getTextListView(item, LayoutInflater.from(context), null, false)
                else -> getListView(item, LayoutInflater.from(context), null, false)
            }
            val pxPerLevel = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10.0f,
                    context.resources.displayMetrics
            ).toInt().toFloat()
            var addPadding = level * pxPerLevel
            mArrowView = view.findViewById(R.id.arrow_img)
            if (item !is ListItemRecursive) {
                val pxLeaf = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5.0f,
                        context.resources.displayMetrics
                ).toInt().toFloat()
                addPadding += pxLeaf
            } else if (item.subTree == null) {
                mArrowView?.visibility = View.INVISIBLE
            }
            if (Build.VERSION.SDK_INT >= 17) {
                view.setPaddingRelative(view.paddingStart + addPadding.toInt(),
                        view.paddingTop, view.paddingEnd, view.paddingBottom)
            } else {
                view.setPadding(view.paddingLeft + addPadding.toInt(),
                        view.paddingTop, view.paddingRight, view.paddingBottom)
            }
            return view
        }

        override fun toggle(active: Boolean) {
            if (mArrowView == null)
                return
            val a = context.obtainStyledAttributes(intArrayOf(if (active) R.attr.DrawableOpenIndicator else R.attr.DrawableClosedIndicator))

            mArrowView?.setImageResource(a.getResourceId(0,
                    if (active)
                        R.drawable.expander_open_holo_dark
                    else
                        R.drawable.expander_close_holo_dark))
            a.recycle()
            mArrowView?.contentDescription =   if (active) "Collapsed list" else "Open list"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = TreeNode.root()

        for (item in items)
            root.addChild(getTreeNode(item, 0))

        val tv = AndroidTreeView(activity, root)
        tView = tv
        tv.setDefaultAnimation(true)
        tv.setDefaultViewHolder(ListItemHolder::class.java)
        tv.setDefaultNodeClickListener(this)

        if (savedInstanceState != null) {
            val state = savedInstanceState.getString("tState")
            if (!TextUtils.isEmpty(state)) {
                tv.restoreState(state)
            }
        }

        return tv.view
    }

    private fun getTreeNode(item: ListItemInterface, level: Int): TreeNode {
        if (item !is ListItemRecursive)
            return TreeNode(Pair.create(item, level))
        val root = TreeNode(Pair.create<ListItemInterface, Int>(item, level))
        for (subItem in item.subTree.orEmpty()) {
            root.addChild(getTreeNode(subItem, level + 1))
        }
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }
}
