package contracts.user


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "change_password_success"

    request {
        method 'POST'
        url '/users/change-password'
        headers {
            contentType(applicationJson())
            header 'Authorization': 'Bearer 123'
        }
        body(
                password         : "123",
                confirm_password : "123"
        )
    }

    response {
        status 200
    }
}
