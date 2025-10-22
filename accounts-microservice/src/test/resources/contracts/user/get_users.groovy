package contracts.user


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "get_users_success"

    request {
        method 'GET'
        url '/users'
        headers {
            header 'Authorization': 'Bearer 123'
        }
    }

    response {
        status 200
        body([
                [
                        email   : "user@example.com",
                        username: "User One"
                ],
                [
                        email   : "user2@example.com",
                        username: "User Two"
                ]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}