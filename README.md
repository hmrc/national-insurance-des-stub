# National Insurance DES Stub

[![Build Status](https://travis-ci.org/hmrc/national-insurance-des-stub.svg)](https://travis-ci.org/hmrc/national-insurance-des-stub)

The National Insurance DES Stub is a service to support stateful sandbox testing. It stubs the behaviour of DES for calls in the National Insurance domain so that API
microservices can be configured to call the stub instead of real DES. Additionally, it exposes a test-support API
that is published to the [Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/national-insurance-des-stub) for third-party developers to be able to set up scenarios for testing.

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

To run the service locally on port `9650`:
```
./run_in_stub_mode.sh
```
