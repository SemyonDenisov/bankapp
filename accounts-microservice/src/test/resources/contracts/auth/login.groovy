package contracts.auth


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "login_success"

    request {
        method 'POST'
        url '/login'
        headers {
            contentType(applicationJson())
        }
        body(
                email   : "user@example.com",
                password: "123"
        )
    }

    response {
        status 200
        body(
                token: $(regex('.+'))
        )
        headers {
            contentType(applicationJson())
        }
    }
}
