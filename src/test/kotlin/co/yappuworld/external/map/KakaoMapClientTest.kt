package co.yappuworld.external.map

import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KakaoMapClientTest(
    private val kakaoMapClient: KakaoMapClient
) : SpringBootTestFeatureSpec({

        feature("KakaoMapClient") {
            scenario("양녕로 117 주소를 좌표로 변환한다") {
                val address = "양녕로 117"
                val expectedLatitude = 37.4915087835926
                val expectedLongitude = 126.942726707191

                val result = kakaoMapClient.convertAddressToCoordinates(address)

                result.addressName shouldBe "서울 관악구 양녕로 117"
                result.latitude shouldBe expectedLatitude
                result.longitude shouldBe expectedLongitude
            }
        }
    })
