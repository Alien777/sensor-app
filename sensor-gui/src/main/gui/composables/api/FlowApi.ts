import type {FlowT, Node} from "~/composables/api/StructureApp";

export const flowApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);
    const saveFlow = async (flow: FlowT): Promise<void> => {
        await fetchApiRequest<void>("/flow",
            {
                method: 'post', body: flow
            });
    }

    const startFlow = async (id: number): Promise<void> => {
        await fetchApiRequest<void>("/flow/start",
            {
                method: 'post', body: '' + id
            });
    }

    const stopFlow = async (id: number): Promise<void> => {
        await fetchApiRequest<void>("/flow/stop",
            {
                method: 'delete', body: '' + id
            });
    }

    const getAll = async (): Promise<Array<FlowT>> => {
        let value = await fetchApiRequest<Array<FlowT>>("/flow",
            {
                method: 'get'
            });
        return value.data.value;
    }

    const get = async (id: number): Promise<Array<FlowT>> => {
        let value = await fetchApiRequest<Array<FlowT>>(`/flow/${id}`,
            {
                method: 'get'
            });
        return value.data.value;
    }


    return {
        saveFlow, startFlow, stopFlow, getAll, get
    }


}


