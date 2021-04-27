/*
 * Copyright 2021 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 */

package com.linecorp.armeria.server

import com.linecorp.armeria.client.WebClient
import com.linecorp.armeria.common.MediaType
import com.linecorp.armeria.server.annotation.Post
import com.linecorp.armeria.server.annotation.ProducesJson
import com.linecorp.armeria.testing.junit5.server.ServerExtension
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class JacksonModuleAnnotatedServiceTest {

    @Test
    fun shouldEncodeAndDecodeDataClassWithJson() {
        val client = WebClient.of(server.httpUri())
        val json = """{"x": 10, "y":"hello"}"""
        val response = client.prepare()
            .post("/echo")
            .content(MediaType.JSON, json)
            .execute().aggregate().join()
        assertThatJson(response.contentUtf8()).isEqualTo(json)
    }

    companion object {
        @JvmField
        @RegisterExtension
        val server: ServerExtension = object : ServerExtension() {
            override fun configure(sb: ServerBuilder) {
                sb.annotatedService(ServiceWithDataClass())
            }
        }
    }
}

class ServiceWithDataClass {

    @ProducesJson
    @Post("/echo")
    fun echo(foo: Foo): Foo {
        return foo
    }
}

data class Foo(val x: Int, val y: String)
