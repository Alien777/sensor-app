import {type FlowT, StatusOfFlow} from "~/composables/api/StructureApp";

export const flowApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);
    const saveFlow = async (flow: FlowT): Promise<void> => {
        await fetchApiRequest<void>("/flow",
            {
                method: 'post', body: flow
            });
    }

    const startFlow = async (id: number): Promise<StatusOfFlow> => {
        let value = await fetchApiRequest<StatusOfFlow>(`/flow/start/${id}`,
            {
                method: 'post'
            });
        return value.data.value;
    }

    const stopFlow = async (id: number): Promise<StatusOfFlow> => {
        let value = await fetchApiRequest<StatusOfFlow>(`/flow/stop/${id}`,
            {
                method: 'delete', body: '' + id
            });
        return value.data.value;
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

    const deleteFlow = async (id: number): Promise<StatusOfFlow> => {
        let value = await fetchApiRequest<StatusOfFlow>(`/flow/${id}`,
            {
                method: 'delete'
            });
        return value.data.value;
    }

    return {
        saveFlow, startFlow, stopFlow, getAll, get, deleteFlow
    }


}


