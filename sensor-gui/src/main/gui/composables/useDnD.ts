import {type GraphNode, useVueFlow, type XYPosition} from '@vue-flow/core'
import {ref, watch} from 'vue'
import type {NodeDraggable} from "~/composables/api/StructureApp";

let id = 0

/**
 * @returns {string} - A unique id.
 */
function getId(name: string) {
    return `${name}_${id++}`
}


const state = {
    /**
     * The type of the node being dragged.
     */
    isDragOver: ref(false),
    isDragging: ref(false),
}

export default function useDragAndDrop() {
    const {isDragOver, isDragging} = state

    const {addNodes, screenToFlowCoordinate, onNodesInitialized, updateNode} = useVueFlow()

    watch(isDragging, (dragging) => {
        document.body.style.userSelect = dragging ? 'none' : ''
    })

    function onDragStart(event: DragEvent, type: NodeDraggable) {
        if (event.dataTransfer) {
            event.dataTransfer.setData('application/vueflow', JSON.stringify(type))
            event.dataTransfer.effectAllowed = 'move'
        }
        isDragging.value = true
        document.addEventListener('drop', onDragEnd)
    }

    function onDragOver(event: DragEvent) {
        event.preventDefault()
        isDragOver.value = true
        if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'move'
        }
    }

    function onDragLeave() {
        isDragOver.value = false
    }

    function onDragEnd() {
        isDragging.value = false
        isDragOver.value = false
        document.removeEventListener('drop', onDragEnd)
    }

    /**
     * Handles the drop event.
     *
     * @param {DragEvent} event
     */
    function onDrop(event: DragEvent) {
        const position: XYPosition = screenToFlowCoordinate({
            x: event.clientX,
            y: event.clientY,
        })
        if (event.dataTransfer) {

            const nodeDraggable = JSON.parse(event.dataTransfer.getData('application/vueflow')) as NodeDraggable;

            const nodeId = getId(nodeDraggable.name)

            const component = defineAsyncComponent(() =>
                import(`~/components/flows/nodes/${nodeDraggable.name}.vue`)
            )

            const wrap = defineComponent({
                name: 'NodeWrap',
                setup(props, {slots}) {
                    return () =>
                        h('div', {style: {height: '100%', width: '100%'}},
                            h(component, {
                                id: nodeId
                            }, slots.default ? slots.default() : [])
                        );
                },
            });


            const newNode: any | Node | Node[] | ((nodes: GraphNode[]) => Node | Node[]) = {
                id: nodeId,
                type: nodeDraggable.type,
                position,
                width: 230,
                data: {
                    label: wrap,
                }
            }
            const {off} = onNodesInitialized(() => {
                updateNode(nodeId, (node) => ({
                    position: {
                        x: node.position.x - node.dimensions.width / 2,
                        y: node.position.y - node.dimensions.height / 2
                    },
                }))

                off()
            })

            addNodes(newNode)
        }
    }

    return {
        isDragOver,
        isDragging,
        onDragStart,
        onDragLeave,
        onDragOver,
        onDrop,
    }
}
