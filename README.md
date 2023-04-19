# National Insurance DES Stub

The National Insurance DES Stub is a service to support stateful sandbox testing. It stubs the behaviour of DES for calls in the National Insurance domain so that API
microservices can be configured to call the stub instead of real DES. Additionally, it exposes a test-support API
that is published to the [Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/national-insurance-des-stub) for third-party developers to be able to set up scenarios for testing.

## Viewing Documentation
### Locally
- Run National Insurance DES Stub and other required services with the script:

    ```
     ./run_local_preview_documentation.sh
    ```

- Navigate to the preview page at http://localhost:9680/api-documentation/docs/openapi/preview
- Enter the full URL path of the OpenAPI specification file with the appropriate port and version:

    ```
     http://localhost:9688/api/conf/1.0/application.yaml
    ```
- Ensure to uncomment the lines [here](https://github.com/hmrc/national-insurance-des-stub/blob/main/conf/application.conf#L45-L48) in case of CORS errors

### On Developer Hub
Full documentation can be found on the [Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/national-insurance-des-stub).

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")


## What uses this service?
API microservices that make calls to DES for National Insurance data should be configured to connect to this stub
instead of to a real DES when they are deployed to the External Test environment

## What does this service use?
* Datastream
* Metrics/Grafana

## Running the tests
```
./run_all_tests.sh
```

## Running the service locally

To run the service locally on port `9688`:
```
./run_in_stub_mode.sh
```
