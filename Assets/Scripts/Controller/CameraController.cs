﻿using UnityEngine;

public class CameraController : MonoBehaviour
{
	public Transform target;
	private float x, y = 0;
	private float _minDistance = 1;
	private float _maxDistance = 10f;
	public Vector3 camOffset = new Vector3();
	public float camDistance = 5f;
	public float currentDistance = 0;
	public float camSpeed = 25f;

	public Vector3 targetPos;

	public CameraCollisionDetection detector;

    void Start() {
        detector = new CameraCollisionDetection(GetComponent<Camera>(), target, camOffset);
    }

	void Update() {
		detector.DetectCollision(camDistance);
		x += Input.GetAxis ("Mouse X") * camSpeed * 0.1f;
		y -= Input.GetAxis ("Mouse Y") * camSpeed * 0.1f;
		y = ClampAngle(y, -90, 90);
		camDistance = Mathf.Clamp(camDistance - Input.GetAxis("Mouse ScrollWheel")*5, _minDistance, _maxDistance);	
	}

    void FixedUpdate() {
		Quaternion rotation = Quaternion.Euler(y, x, 0);
		transform.rotation = rotation;			
		
		float adjustedDistance = detector.GetCameraDistance();
		
		if(adjustedDistance > Vector3.Distance(targetPos + camOffset, transform.position)) {
			currentDistance += ((adjustedDistance - currentDistance) / 0.2f) * Time.deltaTime;
		} else {
			currentDistance -= ((currentDistance - adjustedDistance) / 0.075f) * Time.deltaTime;
		}

		
		
		targetPos = target.position + camOffset;
		Vector3 adjustedPosition = rotation * (Vector3.forward * -currentDistance) + targetPos;

		transform.position = adjustedPosition;		
    }

	private float ClampAngle(float angle, float min, float max) {
		if (angle < -360F)
			angle += 360F;
		if (angle > 360F)
			angle -= 360F;
		return Mathf.Clamp(angle, min, max);
	}
}