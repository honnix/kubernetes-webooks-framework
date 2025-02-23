package io.javaoperatorsdk.webhook.sample.admission;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.webhook.admission.AdmissionController;
import io.javaoperatorsdk.webhook.admission.AsyncAdmissionController;
import io.smallrye.mutiny.Uni;

@Path("/")
public class AdmissionAdditionalTestEndpoint {

  public static final String ERROR_ASYNC_MUTATE_PATH = "error-async-mutate";
  public static final String ERROR_ASYNC_VALIDATE_PATH = "error-async-validate";
  public static final String ERROR_MUTATE_PATH = "error-mutate";
  public static final String ERROR_VALIDATE_PATH = "error-validate";

  private final AdmissionController<Ingress> errorMutationController;
  private final AdmissionController<Ingress> errorValidationController;
  private final AsyncAdmissionController<Ingress> errorAsyncMutationController;
  private final AsyncAdmissionController<Ingress> errorAsyncValidationController;

  @Inject
  public AdmissionAdditionalTestEndpoint(
      @Named(AdditionalAdmissionConfig.ERROR_MUTATING_CONTROLLER) AdmissionController<Ingress> errorMutationController,
      @Named(AdditionalAdmissionConfig.ERROR_VALIDATING_CONTROLLER) AdmissionController<Ingress> errorValidationController,
      @Named(AdditionalAdmissionConfig.ERROR_ASYNC_MUTATING_CONTROLLER) AsyncAdmissionController<Ingress> errorAsyncMutationController,
      @Named(AdditionalAdmissionConfig.ERROR_ASYNC_VALIDATING_CONTROLLER) AsyncAdmissionController<Ingress> errorAsyncValidationController) {
    this.errorMutationController = errorMutationController;
    this.errorValidationController = errorValidationController;
    this.errorAsyncMutationController = errorAsyncMutationController;
    this.errorAsyncValidationController = errorAsyncValidationController;
  }

  @POST
  @Path(ERROR_ASYNC_MUTATE_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<AdmissionReview> errorAsyncMutate(AdmissionReview admissionReview) {
    return Uni.createFrom()
        .completionStage(() -> this.errorAsyncMutationController.handle(admissionReview));
  }

  @POST
  @Path(ERROR_ASYNC_VALIDATE_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<AdmissionReview> errorAsyncValidate(AdmissionReview admissionReview) {
    return Uni.createFrom()
        .completionStage(() -> this.errorAsyncValidationController.handle(admissionReview));
  }

  @POST
  @Path(ERROR_MUTATE_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public AdmissionReview errorMutate(AdmissionReview admissionReview) {
    return errorMutationController.handle(admissionReview);
  }

  @POST
  @Path(ERROR_VALIDATE_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public AdmissionReview errorValidate(AdmissionReview admissionReview) {
    return errorValidationController.handle(admissionReview);
  }
}
