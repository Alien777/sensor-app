import {type Edge, type GraphNode, useVueFlow, type XYPosition} from '@vue-flow/core'
import {ref, watch} from 'vue'
import type {Node, NodeDraggable} from "~/composables/api/StructureApp";

let global_id: number = 0

/**
 * @returns {string} - A unique id.
 */
function getId(name: string) {
    return `${name}_${global_id++}`
}

function extractNumberFromString(str: string) {
    const result = /(\d+)$/.exec(str);
    return result ? Number(result[1]) : null;
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

    const {addNodes, addEdges, screenToFlowCoordinate, onNodesInitialized, updateNode} = useVueFlow()

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
            insertNode(nodeDraggable, nodeId, position);
        }
    }

    function insertEdge(edge: Edge) {
        addEdges(edge);
    }

    function insertNode(insert: Node | NodeDraggable, id: string, position: XYPosition | any) {
        const max_id = extractNumberFromString(id);
        global_id = 0;
        if (max_id && max_id >= global_id) {
            global_id = max_id + 1;
        }
        const component = defineAsyncComponent(() =>
            import(`~/components/flows/nodes/${insert.name}.vue`)
        )

        const wrap = defineComponent({
            name: 'NodeWrap',
            setup(props, {slots}) {
                const copyIdToClipboard = () => {
                    navigator.clipboard.writeText(id).then(() => {
                    }).catch(err => {
                        console.error('Failed to copy ID to clipboard', err);
                    });
                };
                return () =>
                    h('div', {style: {height: '100%', width: '100%'}}, [
                        h('strong', {onClick: copyIdToClipboard}, id),
                        h(component, {
                            id: id,
                            sensor: insert.sensor
                        }, slots.default ? slots.default() : [])]
                    );
            },
        });


        const newNode: any | Node | Node[] | ((nodes: GraphNode[]) => Node | Node[]) = {
            id: id,
            name: insert.name,
            type: insert.type,
            position: position,
            width: 230,
            data: {
                label: wrap,
            }
        }

        const {off} = onNodesInitialized(() => {
            updateNode(id, (node) => ({
                position: {
                    x: node.position.x - node.dimensions.width / 2,
                    y: node.position.y - node.dimensions.height / 2
                },
            }))

            off()
        })

        addNodes(newNode)
    }

    return {
        isDragOver,
        isDragging,
        onDragStart,
        onDragLeave,
        onDragOver,
        onDrop,
        insertNode,
        insertEdge
    }
}
