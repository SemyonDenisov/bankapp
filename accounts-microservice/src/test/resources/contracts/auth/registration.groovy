package contracts.auth


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "registration_success"

    request {
        method 'POST'
        url '/registration'
        headers {
            contentType(applicationJson())
        }
        body(
                email            : "newuser@example.com",
                username         : "newuser",
                password         : "123",
                confirm_password : "123"
        )
    }

    response {
        status 200
        body(true)
        headers {
            contentType(applicationJson())
        }
    }
}
