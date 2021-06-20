﻿using UnityEngine;

public class PlayerController : MonoBehaviour {

	/* Components */
	private CharacterController controller;

	/*Rotate*/
	private float _finalAngle;

	/* Move */
	public Vector3 moveDirection;
	public float _currentSpeed;
	public float defaultSpeed = 4;
	private Vector2 _axis;

	/* Gravity */ 
	private float _verticalVelocity = 0;
	public float _jumpForce = 10;
	public float _gravity = 28;

	private NetworkTransform networkTransform;
	public static PlayerController _instance;
    public static PlayerController GetInstance() {
        return _instance;
    }

    void Awake() {
        if (_instance == null) {
            _instance = this;
        }
    }

	void Start () {
		controller = GetComponent<CharacterController>();
		networkTransform = GetComponent<NetworkTransform>();
	}

	public void UpdateInputs(float x, float y) {
		_axis = new Vector2(x, y);
	}

	void FixedUpdate () {
        /* Speed */
        _currentSpeed = GetMoveSpeed (_currentSpeed);

        /* Angle */
		_finalAngle = GetRotationValue (_finalAngle);
        transform.rotation = Quaternion.Lerp (transform.rotation, Quaternion.Euler (Vector3.up * _finalAngle), Time.deltaTime * 7.5f);

        /* Direction */
		moveDirection = GetMoveDirection (moveDirection, _currentSpeed);
        controller.Move (moveDirection * Time.deltaTime);
	}

	private float GetRotationValue(float angle) {
		float startAngle = angle;
		if (KeyPressed()) {
            angle = Mathf.Atan2 (_axis.x, _axis.y) * Mathf.Rad2Deg;
            angle = Mathf.Round (angle / 45f);
            angle *= 45f;
            angle += Camera.main.transform.eulerAngles.y;
		}	

		if(startAngle != angle && networkTransform.isActiveAndEnabled) {
			networkTransform.ShareRotation(angle);
		}

        return angle;	
	}

	private Vector3 GetMoveDirection(Vector3 direction, float speed) {
        Vector3 forward = Camera.main.transform.TransformDirection (Vector3.forward);
        forward.y = 0;
        Vector3 right = new Vector3 (forward.z, 0, -forward.x);

		/* Handle gravity */
		if (controller.isGrounded) {
			if(_verticalVelocity < -1.25f) {
				_verticalVelocity = -1.25f; 	
			}
		} else {
			_verticalVelocity -= _gravity * Time.deltaTime; 
		}

		/* Handle input direction */
		if (KeyPressed () && controller.isGrounded) {
			direction = _axis.x * right + _axis.y * forward;		
		} else if (!controller.isGrounded) {
			direction = transform.forward;
		}

		direction = direction.normalized * speed;	
		
		direction.y = _verticalVelocity;	

        return direction;
	}

	private float GetMoveSpeed(float speed) {
		float smoothDuration = 0.2f;

		if(KeyPressed()) {
			speed = defaultSpeed;
		} else if(speed > 0 && controller.isGrounded) {
			speed -= (defaultSpeed/smoothDuration) * Time.deltaTime;
		}

        return speed < 0 ? 0 : speed;
	}

	private bool KeyPressed() {
        return _axis.x != 0 || _axis.y != 0;
	}

	public void Jump() {
		if(controller.isGrounded) {
			_verticalVelocity = _jumpForce;
		}		
	}
}