package jpm.lib.maps

import jpm.lib.math.Vector2D
import jpm.lib.math.nearZero

/**
 * Created by jm on 17/03/17.
 *
 * http://www.dcc.fc.up.pt/~pribeiro/aulas/taa1415/rangesearch.pdf
 */
object KDTreeAbs {
    enum class SplitAxis(val i: Int) {
        XAxis(0), YAxis(1)
    }

    val dirLabels = arrayOf("T_R", "B_L")

    val TOP_OR_RIGHT = 0
    val BOTTOM_OR_LEFT = 1

    val otherChild = arrayOf(1,0)

    var thresholdPos = 0.9
    var thresholdNeg = 1.0 - thresholdPos

    fun setThreshold(threshold: Double) {
        thresholdPos = threshold
        thresholdNeg = 1.0 - threshold
    }

    val maxChildren = 2

    abstract class Node<T : Node<T, V, N>, V : Vector2D<V, N>, N : Number>(override val center: V, val splitAxis: SplitAxis, val xDim: N, val yDim: N = xDim) : Tree<T, V, N> {

        override fun visitAll(visit: (node: T) -> Unit) {
            visit(this as T)

            for (child in children)
                child?.visitAll(visit)
        }

        override fun visitAllDepthFirst(visit: (node: T) -> Unit) {
            for (child in children)
                child?.visitAll(visit)

            visit(this as T)
        }

        override fun size(): Int {
            var s = 1
            for (child in children) {
                if (child != null) s += child.size()
            }
            return s
        }

        override fun occupied(p: V): Double = getPoint(p).occupied()
        override fun free(p: V): Double = getPoint(p).free()
        override fun free(): Double = 1.0 - occupied()

        internal var samplesPositive = 0
        internal var samplesNegative = 0

        override var isLeaf = true

        protected fun setOccupied() {
            if (samplesPositive < Int.MAX_VALUE) ++samplesPositive
            else if (samplesNegative > 0) --samplesNegative
        }

        protected fun setFree() {
            if (samplesNegative < Int.MAX_VALUE) ++samplesNegative
            else if (samplesPositive > 0) --samplesPositive
        }

        protected fun meanOccupied(): Double {

            var occupied = 0.0
            var count = 0.0

            for (child in children) {
                if (child != null) {
                    occupied += child.occupied()
                    count += 1.0
                }
            }
            if (count == 0.0) return 0.5 else return (occupied + 0.5 * (maxChildren - count)) / maxChildren
        }

        override fun occupied(): Double {
            if (isLeaf) {
                if (samplesPositive > samplesNegative)
                    return samplesPositive / (samplesPositive.toDouble() + samplesNegative)
                else if (samplesPositive < samplesNegative)
                    return 1.0 - samplesNegative / (samplesPositive.toDouble() + samplesNegative)
                else
                    return 0.5
            }
            return meanOccupied()
        }

        protected fun tryDeleteChild() {

            if(nearZero(children[0]!!.occupied() - 0.5, 1E-10) &&
               nearZero(children[1]!!.occupied() - 0.5, 1E-10)) {
                isLeaf = true
                samplesNegative = 0
                samplesPositive = 0
            }
        }

        protected fun tryMergeChildren() {

            fun deleteAllChildren() {
                children[0] = null
                children[1] = null
                isLeaf = true
            }

            var numberOfOccupied = 0
            var numberOfFree = 0

            for (child in children) {
                if (child!!.isLeaf) {
                    if (child.occupied() >= thresholdPos) ++numberOfOccupied
                    if (child.free() >= thresholdPos) ++numberOfFree
                } else break
            }
            if (numberOfOccupied == maxChildren || numberOfFree == maxChildren) {
                var positives = 0
                var negatives = 0

                for (child in children) {
                    var toSubtractNeg = 0
                    var toSubtractPos = 0

                    if (positives < Int.MAX_VALUE - child!!.samplesPositive) {
                        positives += child.samplesPositive
                    } else {
                        toSubtractNeg = child.samplesPositive + (positives - Int.MAX_VALUE)
                        positives = Int.MAX_VALUE
                    }

                    if (negatives < Int.MAX_VALUE - child.samplesNegative) {
                        negatives += child.samplesNegative
                    } else {
                        toSubtractPos = child.samplesNegative + (negatives - Int.MAX_VALUE)
                        negatives = Int.MAX_VALUE
                    }

                    negatives -= toSubtractNeg
                    if (negatives < 0) negatives = 0

                    positives -= toSubtractPos
                    if (positives < 0) positives = 0
                }
                samplesPositive = positives
                samplesNegative = negatives

                deleteAllChildren()
            }
        }

        override fun setFree(p: V) {

            if (canSplit()) {
                if (samplesPositive > 0 || samplesNegative > 0) {
                    if (samplesPositive < samplesNegative) {
                        setFree()
                    } else {
                        getOrCreateChild(p).setFree(p)
                        split()
                    }
                } else {
                    val child = getOrCreateChild(p)
                    child.setFree(p)
                    tryMergeChildren()
                    if (child.isLeaf && nearZero(child.occupied() - 0.5, 1E-10))
                        tryDeleteChild()
                }
            } else
                setFree()
        }

        override fun setOccupied(p: V) {

            if (canSplit()) {
                if (samplesPositive > 0 || samplesNegative > 0) {
                    if (samplesPositive > samplesNegative) {
                        setOccupied()
                    } else {
                        getOrCreateChild(p).setOccupied(p)
                        split()
                    }
                } else {
                    val child = getOrCreateChild(p)
                    child.setOccupied(p)
                    if (child.isLeaf) {
                        tryMergeChildren()
                        if(nearZero(child.occupied() - 0.5, 1E-10))
                           tryDeleteChild()
                    }
                }
            } else
                setOccupied()
        }

        fun setOccupied(x: N, y: N) {

            if (canSplit()) {
                if (samplesPositive > 0 || samplesNegative > 0) {
                    if (samplesPositive > samplesNegative) {
                        setOccupied()
                    } else {
                        getOrCreateChild(x,y).setOccupied(x,y)
                        split()
                    }
                } else {
                    val child = getOrCreateChild(x,y)
                    child.setOccupied(x,y)
                    if (child.isLeaf) {
                        tryMergeChildren()
                        if(nearZero(child.occupied() - 0.5, 1E-10))
                            tryDeleteChild()
                    }
                }
            } else
                setOccupied()
        }

        protected abstract fun getOrCreateChild(p: V): T
        protected abstract fun getOrCreateChild(x: N, y: N): T

        protected abstract fun canSplit(): Boolean
        protected abstract fun split()
        abstract val children: Array<T?>
        protected abstract val halfDim: N

        override fun toString(): String = toString("")

        private fun toString(spacing: String): String {
            fun toStringChild(pos: Int, s: String): String {
                if (children[pos] != null)
                    return "\n$s${dirLabels[pos]} -> " + children[pos]!!.toString(s + "  ")
                return ""
            }

            var res = "Node($center,$splitAxis,$xDim,$yDim,$samplesPositive,$samplesNegative,$isLeaf, ${occupied()})"
            if (isLeaf) return res

            res += toStringChild(TOP_OR_RIGHT, spacing + "  ")
            res += toStringChild(BOTTOM_OR_LEFT, spacing + "  ")

            return res
        }

        override fun toStringLeaf(): String {
            fun toStringChild(pos: Int): String {
                if (children[pos] != null)
                    return children[pos]!!.toStringLeaf()
                else return ""
            }

            if (isLeaf) return "\nNode($center,$splitAxis,$xDim,$yDim,$samplesPositive,$samplesNegative,$isLeaf, ${occupied()})"

            return toStringChild(TOP_OR_RIGHT) + toStringChild(BOTTOM_OR_LEFT)
        }

        override fun toStringNode(): String
                = "Node($center,$splitAxis,$xDim,$yDim,$samplesPositive,$samplesNegative,$isLeaf, ${occupied()})"
    }
}