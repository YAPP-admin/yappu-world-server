package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.MasterAlarmToggleAppResponse

data class MasterAlarmToggleApiResponse(
    val isEnabled: Boolean
) {
    companion object {
        fun of(response: MasterAlarmToggleAppResponse): MasterAlarmToggleApiResponse {
            return MasterAlarmToggleApiResponse(response.isEnabled)
        }
    }
}
