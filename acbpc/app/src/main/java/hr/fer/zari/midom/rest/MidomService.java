package hr.fer.zari.midom.rest;

import hr.fer.zari.midom.rest.request.*;
import hr.fer.zari.midom.rest.response.*;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedFile;

import java.util.List;

public interface MidomService {

    @POST("/ms/login")
    LoginResponse msLogin(@Body LoginRequest loginRequest);

    @POST("/ms/login")
    void msLogin(@Body LoginRequest loginRequest, Callback<LoginResponse> cb);

    @GET("/ms/getCr/{status}")
    ConsultationRequestsResponse getConsultationRequestsByStatus(@Path("status") String status);

    @GET("/ms/getCr/{status}")
    void getConsultationRequestsByStatus(@Path("status") String status, Callback<ConsultationRequestsResponse> cb);

    @POST("/ms/acceptRequest")
    AcceptConsultationResponse acceptConsultationRequest(@Body AcceptConsultationRequest acceptConsultationRequest);

    @POST("/ms/acceptRequest")
    void acceptConsultationRequest(@Body AcceptConsultationRequest acceptConsultationRequest, Callback<AcceptConsultationResponse> cb);

    @POST("/ms/rejectRequest")
    RejectConsultationResponse rejectConsultationRequest(@Body RejectConsultationRequest rejectConsultationRequest);

    @POST("/ms/rejectRequest")
    void rejectConsultationRequest(@Body RejectConsultationRequest rejectConsultationRequest, Callback<RejectConsultationResponse> cb);

    @GET("/ms/accountDetails")
    AccountDetailsResponse getAccountDetails();

    @GET("/ms/accountDetails")
    void getAccountDetails(Callback<AccountDetailsResponse> cb);

    @POST("/ms/changeAccountDetails")
    ChangeAccountDetailsResponse updateAccountDetails(@Body ChangeAccountDetailsRequest changeAccountDetailsRequest);

    @POST("/ms/changeAccountDetails")
    void updateAccountDetails(@Body ChangeAccountDetailsRequest changeAccountDetailsRequest, Callback<ChangeAccountDetailsResponse> cb);

    @POST("/ms/changeStatus")
    ChangeStatusResponse changeStatus(@Body ChangeStatusRequest changeStatusRequest);

    @POST("/ms/changeStatus")
    void changeStatus(@Body ChangeStatusRequest changeStatusRequest, Callback<ChangeStatusResponse> cb);

    @POST("/ms/setCrAnswer")
    SetConsultationAnswerResponse setCrAnswer(@Body SetConsultationAnswerRequest setConsultationAnswerRequest);

    @POST("/ms/setCrAnswer")
    void setCrAnswer(@Body SetConsultationAnswerRequest setConsultationAnswerRequest, Callback<SetConsultationAnswerResponse> cb);

    @GET("/ms/getStudy/{id}")
    GetStudyResponse getStudy(@Path("id") int id);

    @GET("/ms/getStudy/{id}")
    void getStudy(@Path("id") int id, Callback<GetStudyResponse> cb);

    @GET("/ms/getAccount/{id}")
    AccountDetailsResponse getAccount(@Path("id") int id);

    @GET("/ms/getAccount/{id}")
    void getAccount(@Path("id") int id, Callback<AccountDetailsResponse> cb);

    @GET("/ms/geCrMessages/{id}")
    GetCrMesssagesResponse getCrMessages(@Path("id") int id);

    @GET("/ms/geCrMessages/{id}")
    void getCrMessages(@Path("id") int id, Callback<GetCrMesssagesResponse> cb);

    @GET("/ms/getAllSpecialisations")
    GetAllSpecialisationsResponse getAllSpecialisations();

    @GET("/ms/getAllSpecialisations")
    void getAllSpecialisations(Callback<GetAllSpecialisationsResponse> cb);

    @POST("/ms/updateSpecialisations")
    UpdateSpecialisationsResponse updateSpecialisations(@Body List<Integer> ids);

    @POST("/ms/updateSpecialisations")
    void updateSpecialisations(@Body List<Integer> ids, Callback<UpdateSpecialisationsResponse> cb);

    @POST("/ms/changePassword")
    ChangePasswordResponse changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("/ms/changePassword")
    void changePassword(@Body ChangePasswordRequest changePasswordRequest, Callback<ChangePasswordResponse> cb);

    @Multipart
    @POST("/ms/uploadAudioFile/{id}")
    void uploadAudioFile(@Path("id") int id, @Part("body") TypedFile file);

    @Multipart
    @POST("/ms/uploadAudioFile/{id}")
    void uploadAudioFile(@Path("id") int id, @Part("body") TypedFile file, Callback<Void> cb);

    @Multipart
    @POST("/ms/changeAvatar")
    void changeAvatar(@Part("body") TypedFile file);

    @Multipart
    @POST("/ms/changeAvatar")
    void changeAvatar(@Part("body") TypedFile file, Callback<Void> cb);

    @GET("/avatar/{id}")
    @Headers({"Content-Type: image/jpeg"})
    void getAvatar(@Path("id") int accountId, Callback<Response> cb);


    @GET("/ms/avatar/{id}")
    @Headers({"Content-Type: image/jpeg"})
    void getMsAvatar(@Path("id") int accountId, Callback<Response> cb);
}
