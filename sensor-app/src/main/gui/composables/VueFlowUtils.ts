import {type Edge, type GraphNode, useVueFlow, type XYPosition} from '@vue-flow/core'
import {ref, watch} from 'vue'
import type {Node, NodeDraggable} from "~/composables/api/StructureApp";
import NodeModal from "~/components/flows/nodes/NodeModal.vue";
import {draggableItems} from "~/composables/api/StructureApp";

let global_id: number = 0


function getId(name: string): string {
    return `${name}_${global_id++}`
}

function extractNumberFromString(str: string) {
    const result = /(\d+)$/.exec(str);
    return result ? Number(result[1]) : null;
}

const state = {
    isDragOver: ref(false),
    isDragging: ref(false),
}

export default function useDragAndDrop() {
    const {isDragOver, isDragging} = state

    const {
        viewport,
        defaultViewport,
        addNodes,
        addEdges,
        removeNodes,
        screenToFlowCoordinate,
        onNodesInitialized,
        updateNode
    } = useVueFlow()

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

    function setViewPort(view: any) {
        viewport.value = view;
        defaultViewport.value = view;
    }

    function insertEdge(edge: Edge) {
        addEdges(edge);
    }

    function insertNode(insert: Node | NodeDraggable, id: string, position: XYPosition | any) {
        const max_id = extractNumberFromString(id);
        global_id = 0;
        if (max_id != null && max_id >= global_id) {
            global_id = max_id + 1;
        }
        const component = defineAsyncComponent(() =>
            import(`~/components/flows/nodes/${insert.name}.vue`)
        )


        const wrap = defineComponent({
            name: 'NodeWrap',

            setup() {
                return () => {
                    return h('div', {style: {height: '100%', width: '100%'}}, [
                        h(NodeModal, {
                                id: id as any,
                                node: insert,
                                deleteNodeFunction: (id: string) => removeNodes({id: id} as any),
                                nodeDefault: draggableItems.value.filter(value => value.name === insert.name)[0] as any
                            },
                            h(component, {
                                id: id,
                                sensor: insert.sensor
                            })
                        )]
                    );
                }
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
        setViewPort,
        insertNode,
        insertEdge
    }
}
