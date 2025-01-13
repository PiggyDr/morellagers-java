package com.piggydragons.morellagers.client.model;
// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.entities.enemy.ArmoredPillager;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class ArmoredPillagerModel<T extends ArmoredPillager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Morellagers.MOD_ID, "armored_pillager_model"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart root;

	public ArmoredPillagerModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.nose = head.getChild("nose");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
		head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
		hat.addOrReplaceChild("h3lmet_r1", CubeListBuilder.create().texOffs(45, 23).addBox(3.0F, -3.0F, 0.0F, 0.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.6F, -5.05F, -4.5F, 0.0F, 0.0F, 0.7854F));
		hat.addOrReplaceChild("h3lmet_r2", CubeListBuilder.create().texOffs(45, 32).addBox(-6.0F, -3.0F, 0.0F, 9.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.0F, 6.5F, 0.7854F, 0.0F, 0.0F));
		hat.addOrReplaceChild("h3lmet_r3", CubeListBuilder.create().texOffs(45, 23).addBox(3.0F, -3.0F, 0.0F, 0.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -1F, -4.5F, 0.0F, 0.0F, -0.7854F));
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F).texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
				.texOffs(48, 36).mirror().addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-2.0F, 12.0F, 0.0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F).mirror(false)
				.texOffs(48, 36).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 12.0F, 0.0F));
		PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
		PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
		rightArm.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(44, 24).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -2.0F, 0.0F, 0F, 0.0F));
		leftArm.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 24).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 2.0F, 0.0F, 3.1416F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T p_102928_, float p_102929_, float p_102930_, float p_102931_, float p_102932_, float p_102933_) {
		this.head.yRot = p_102932_ * ((float)Math.PI / 180F);
		this.head.xRot = p_102933_ * ((float)Math.PI / 180F);
		if (this.riding) {
			this.rightArm.xRot = (-(float)Math.PI / 5F);
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = (-(float)Math.PI / 5F);
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = ((float)Math.PI / 10F);
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = (-(float)Math.PI / 10F);
			this.leftLeg.zRot = -0.07853982F;
		} else {
			this.rightArm.xRot = Mth.cos(p_102929_ * 0.6662F + (float)Math.PI) * 2.0F * p_102930_ * 0.5F;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = Mth.cos(p_102929_ * 0.6662F) * 2.0F * p_102930_ * 0.5F;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = Mth.cos(p_102929_ * 0.6662F) * 1.4F * p_102930_ * 0.5F;
			this.rightLeg.yRot = 0.0F;
			this.rightLeg.zRot = 0.0F;
			this.leftLeg.xRot = Mth.cos(p_102929_ * 0.6662F + (float)Math.PI) * 1.4F * p_102930_ * 0.5F;
			this.leftLeg.yRot = 0.0F;
			this.leftLeg.zRot = 0.0F;
		}

		AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = p_102928_.getArmPose();
		if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
			if (p_102928_.getMainHandItem().isEmpty()) {
				AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, p_102931_);
			} else {
				AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, p_102928_, this.attackTime, p_102931_);
			}
		} else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;
			this.rightArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.25F;
			this.leftArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.25F;
			this.rightArm.zRot = 2.3561945F;
			this.leftArm.zRot = -2.3561945F;
			this.rightArm.yRot = 0.0F;
			this.leftArm.yRot = 0.0F;
		} else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
			this.rightArm.yRot = -0.1F + this.head.yRot;
			this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
			this.leftArm.xRot = -0.9424779F + this.head.xRot;
			this.leftArm.yRot = this.head.yRot - 0.4F;
			this.leftArm.zRot = ((float)Math.PI / 2F);
		} else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
			AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
		} else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
			AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_102928_, true);
		} else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.CELEBRATING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.rightArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.05F;
			this.rightArm.zRot = 2.670354F;
			this.rightArm.yRot = 0.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;
			this.leftArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.05F;
			this.leftArm.zRot = -2.3561945F;
			this.leftArm.yRot = 0.0F;
		}
	}

	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		(arm == HumanoidArm.LEFT ? leftArm : rightArm).translateAndRotate(poseStack);
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}
}