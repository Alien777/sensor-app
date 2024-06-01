export const userApi = (runtimeConfig: any) => {
    let {fetchApiRequest} = fetchUtils(runtimeConfig);

    const getmemberId = async (): Promise<Array<String>> => {
        let value = await fetchApiRequest<Array<String>>(`/user/member-key`,
            {
                method: 'get'
            });
        return value.data.value;
    }

    return {
        getmemberId
    }

}


