package indi.wenyan.content.entity;

//public class HandRunnerRender extends EntityRenderer<HandRunnerEntity> {
//    private final EntityRenderDispatcher dispatcher;
//
//    public HandRunnerRender(EntityRendererProvider.Context context) {
//        super(context);
//        dispatcher = context.getEntityRenderDispatcher();
//    }
//
//    @Override
//    public @NotNull ResourceLocation getTextureLocation(@NotNull HandRunnerEntity handRunnerEntity) {
//        return ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "item/hand_runner");
//    }
//
//    @Override
//    public void render(
////            @NotNull HandRunnerEntity entityIn, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLightIn
//            EntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
//    ) {
//        super.render(renderState, poseStack, bufferSource, packedLight);
//        poseStack.pushPose();
//        poseStack.translate(0, 0.5, 0);
//        poseStack.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
//        poseStack.scale(0.8F, 0.8F, 0.8F);
//        Minecraft.getInstance().getItemRenderer().renderStatic(
//                new ItemStack(Registration.HAND_RUNNER_1.get()),
//                ItemDisplayContext.FIXED,
//                packedLightIn,
//                OverlayTexture.NO_OVERLAY,
//                poseStack,
//                bufferSource,
//                Minecraft.getInstance().level, 0);
//        poseStack.popPose();
//    }
//}
