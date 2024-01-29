package com.dst.testapp


import com.dst.testapp.atv.model.TreeNode
import com.dst.testapp.sd.CardSerializer
import com.dst.testapp.sd.ListItemInterface

class CardRawDataFragment : TreeListFragment() {
    override val items: List<ListItemInterface>
        get() = CardSerializer.fromPersist(requireArguments().getString(AdvancedCardInfoActivity.EXTRA_CARD)!!).rawData.orEmpty()

    override fun onClick(node: TreeNode, value: Any) {}
}
