package contracts.user


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "edit_user_success"

    request {
        method 'POST'
        url '/users/edit'
        headers {
            contentType(applicationJson())
            header 'Authorization': 'Bearer 123'
        }
        body(
                username: "new_username",
                birthday: "2000-01-01",
                selected_currencies: ["USD", "EUR"]
        )
    }

    response {
        status 200
    }
}
